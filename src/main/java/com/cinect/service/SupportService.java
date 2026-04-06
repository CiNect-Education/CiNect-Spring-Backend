package com.cinect.service;

import com.cinect.dto.request.ContactFormRequest;
import com.cinect.dto.request.ChatbotRequest;
import com.cinect.dto.response.ChatbotResponse;
import com.cinect.entity.SupportTicket;
import com.cinect.entity.enums.MovieStatus;
import com.cinect.repository.BookingRepository;
import com.cinect.repository.CinemaRepository;
import com.cinect.repository.MovieRepository;
import com.cinect.repository.NewsArticleRepository;
import com.cinect.repository.PromotionRepository;
import com.cinect.repository.ShowtimeRepository;
import com.cinect.repository.SupportTicketRepository;
import com.cinect.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PromotionRepository promotionRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final ObjectMapper objectMapper;

    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    @Transactional
    public SupportTicket createTicket(ContactFormRequest req, UUID userId) {
        var user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        var booking = req.getBookingId() != null
                ? bookingRepository.findById(req.getBookingId()).orElse(null) : null;
        var ticket = SupportTicket.builder()
                .user(user)
                .name(req.getName())
                .email(req.getEmail())
                .subject(req.getSubject())
                .category(req.getCategory())
                .message(req.getMessage())
                .booking(booking)
                .isResolved(false)
                .build();
        return supportTicketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public ChatbotResponse chatbot(ChatbotRequest req) {
        String locale = req.getLocale() != null && req.getLocale().toLowerCase().startsWith("en") ? "en" : "vi";
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            return ChatbotResponse.builder()
                    .reply(locale.equals("en")
                            ? "Chatbot is not configured yet. Please set OPENAI_API_KEY on backend."
                            : "Chatbot chưa được cấu hình. Vui lòng thêm OPENAI_API_KEY ở backend.")
                    .build();
        }
        try {
            String context = objectMapper.writeValueAsString(buildChatContext());
            String systemPrompt = locale.equals("en")
                    ? String.join(" ",
                    "You are CiNect professional assistant.",
                    "Priority #1: use CiNect database context for cinema/business questions.",
                    "If user asks general knowledge not in DB, provide a concise helpful answer and explicitly label it as general guidance.",
                    "Never fabricate exact business facts (showtimes, prices, promo codes, movie status). If missing, say not available in current database snapshot.",
                    "Answer in a professional, clear style with short bullet points when useful.")
                    : String.join(" ",
                    "Bạn là trợ lý chuyên nghiệp của CiNect.",
                    "Ưu tiên số 1: dùng context dữ liệu CiNect cho câu hỏi nghiệp vụ rạp.",
                    "Nếu người dùng hỏi kiến thức tổng quát không có trong DB, vẫn trả lời ngắn gọn hữu ích và ghi rõ đó là hướng dẫn chung.",
                    "Không bịa đặt dữ liệu nghiệp vụ cụ thể (suất chiếu, giá, mã khuyến mãi, trạng thái phim). Nếu thiếu dữ liệu, phải nói rõ chưa có trong snapshot hiện tại.",
                    "Trả lời chuyên nghiệp, rõ ràng, ưu tiên gạch đầu dòng khi phù hợp.");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", "gpt-4.1-mini");
            payload.put("input", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", req.getMessage() + "\n\n=== DATABASE CONTEXT ===\n" + context)
            ));
            payload.put("max_output_tokens", 700);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/responses"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openaiApiKey.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("OpenAI upstream error: " + response.statusCode() + " " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String reply = root.path("output_text").asText("");
            if (reply == null || reply.isBlank()) {
                reply = locale.equals("en")
                        ? "No answer generated. Please try again."
                        : "Chưa tạo được phản hồi. Vui lòng thử lại.";
            }
            return ChatbotResponse.builder().reply(reply).build();
        } catch (Exception ex) {
            return ChatbotResponse.builder()
                    .reply(buildFallbackReply(locale, req.getMessage()))
                    .build();
        }
    }

    private String buildFallbackReply(String locale, String message) {
        String q = message == null ? "" : message.toLowerCase(Locale.ROOT);
        Instant now = Instant.now();
        DateTimeFormatter viTime = DateTimeFormatter.ofPattern("HH:mm dd/MM").withZone(ZoneId.systemDefault());
        DateTimeFormatter enTime = DateTimeFormatter.ofPattern("HH:mm dd/MM").withZone(ZoneId.systemDefault());

        if (q.contains("phim") && (q.contains("đang chiếu") || q.contains("dang chieu") || q.contains("now showing"))) {
            var movies = movieRepository.findByStatusAndIsDeletedFalse(MovieStatus.NOW_SHOWING, PageRequest.of(0, 6)).getContent();
            if (movies.isEmpty()) {
                return locale.equals("en")
                        ? "I cannot find any currently showing movies in the current database snapshot."
                        : "Mình chưa thấy phim đang chiếu trong dữ liệu hiện tại.";
            }
            var lines = movies.stream().map(m -> "- " + m.getTitle()).toList();
            return (locale.equals("en")
                    ? "Current now-showing movies:\n"
                    : "Các phim đang chiếu hiện tại:\n") + String.join("\n", lines);
        }

        if (q.contains("khuyến mãi") || q.contains("khuyen mai") || q.contains("promotion")) {
            var promotions = promotionRepository.findActivePromotions(now).stream().limit(5).toList();
            if (promotions.isEmpty()) {
                return locale.equals("en")
                        ? "I cannot find active promotions in the current database snapshot."
                        : "Hiện chưa có khuyến mãi đang hoạt động trong dữ liệu hiện tại.";
            }
            var lines = promotions.stream().map(p -> "- " + p.getTitle() + " (" + (p.getCode() == null ? "N/A" : p.getCode()) + ")").toList();
            return (locale.equals("en")
                    ? "Active promotions:\n"
                    : "Khuyến mãi đang hoạt động:\n") + String.join("\n", lines);
        }

        if (q.contains("lịch chiếu") || q.contains("lich chieu") || q.contains("showtime")
                || q.contains("giờ chiếu") || q.contains("gio chieu")
                || q.contains("rạp") || q.contains("rap")) {
            var showtimes = showtimeRepository.findActiveStartingFrom(now).stream().limit(80).toList();
            if (showtimes.isEmpty()) {
                return locale.equals("en")
                        ? "I cannot find upcoming showtimes in the current database snapshot."
                        : "Mình chưa thấy lịch chiếu sắp tới trong dữ liệu hiện tại.";
            }
            var nowShowingTitles = movieRepository.findByStatusAndIsDeletedFalse(MovieStatus.NOW_SHOWING, PageRequest.of(0, 20))
                    .getContent()
                    .stream()
                    .map(m -> m.getTitle().toLowerCase(Locale.ROOT))
                    .toList();
            var requestedMovieTitles = nowShowingTitles.stream()
                    .filter(q::contains)
                    .collect(Collectors.toCollection(ArrayList::new));

            var filtered = showtimes.stream()
                    .filter(s -> requestedMovieTitles.isEmpty()
                            || requestedMovieTitles.contains(s.getMovie().getTitle().toLowerCase(Locale.ROOT)))
                    .sorted(Comparator.comparing(s -> s.getStartTime()))
                    .toList();

            Map<String, List<String>> grouped = new LinkedHashMap<>();
            for (var s : filtered) {
                String title = s.getMovie().getTitle();
                grouped.putIfAbsent(title, new ArrayList<>());
                if (grouped.get(title).size() >= 3) continue;
                grouped.get(title).add(
                        "- "
                                + (locale.equals("en") ? enTime.format(s.getStartTime()) : viTime.format(s.getStartTime()))
                                + " | " + s.getCinema().getName()
                );
            }

            var movieLines = grouped.entrySet().stream().limit(6)
                    .map(e -> e.getKey() + ":\n" + String.join("\n", e.getValue()))
                    .toList();
            if (movieLines.isEmpty()) {
                return locale.equals("en")
                        ? "I can not find matching showtimes for the movies you asked."
                        : "Mình chưa tìm thấy lịch chiếu khớp với các phim bạn đang hỏi.";
            }
            return (locale.equals("en")
                    ? "Upcoming showtimes by movie:\n"
                    : "Lịch chiếu theo từng phim:\n") + String.join("\n\n", movieLines);
        }

        long movieCount = movieRepository.countByIsDeletedFalse();
        long cinemaCount = cinemaRepository.countByIsActiveTrue();
        long showtimeCount = showtimeRepository.countActiveStartingFrom(now);
        return locale.equals("en")
                ? "I can help with movies, showtimes, cinemas, promotions, and news. Current snapshot: "
                + movieCount + " movies, " + cinemaCount + " cinemas, " + showtimeCount + " upcoming showtimes."
                : "Mình có thể hỗ trợ tra cứu phim, lịch chiếu, rạp, khuyến mãi và tin tức. Dữ liệu hiện tại gồm: "
                + movieCount + " phim, " + cinemaCount + " rạp, " + showtimeCount + " suất chiếu sắp tới.";
    }

    private Map<String, Object> buildChatContext() {
        Instant now = Instant.now();
        var movies = movieRepository.findAllActive(PageRequest.of(0, 20)).getContent().stream()
                .map(m -> Map.of(
                        "title", m.getTitle(),
                        "status", String.valueOf(m.getStatus()),
                        "releaseDate", String.valueOf(m.getReleaseDate()),
                        "duration", m.getDuration(),
                        "ageRating", String.valueOf(m.getAgeRating())
                )).toList();
        var cinemas = cinemaRepository.findAllActive(PageRequest.of(0, 20)).getContent().stream()
                .map(c -> Map.of(
                        "name", c.getName(),
                        "city", c.getCity(),
                        "district", c.getDistrict() == null ? "" : c.getDistrict(),
                        "address", c.getAddress()
                )).toList();
        var showtimes = showtimeRepository.findActiveStartingFrom(now).stream().limit(40)
                .map(s -> Map.of(
                        "startTime", String.valueOf(s.getStartTime()),
                        "format", s.getFormat().getValue(),
                        "language", s.getLanguage() == null ? "" : s.getLanguage(),
                        "movieTitle", s.getMovie().getTitle(),
                        "cinemaName", s.getCinema().getName(),
                        "city", s.getCinema().getCity()
                )).toList();
        var promotions = promotionRepository.findActivePromotions(now).stream().limit(20)
                .map(p -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("title", p.getTitle());
                    item.put("code", p.getCode() == null ? "" : p.getCode());
                    item.put("discountType", String.valueOf(p.getDiscountType()));
                    item.put("discountValue", p.getDiscountValue());
                    item.put("minPurchase", p.getMinPurchase());
                    item.put("maxDiscount", p.getMaxDiscount());
                    item.put("endDate", String.valueOf(p.getEndDate()));
                    return item;
                }).toList();
        var news = newsArticleRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(0, 12)).getContent().stream()
                .map(n -> Map.of(
                        "title", n.getTitle(),
                        "category", String.valueOf(n.getCategory()),
                        "publishedAt", String.valueOf(n.getPublishedAt())
                )).toList();

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("generatedAt", now.toString());
        context.put("totals", Map.of(
                "movies", movieRepository.countByIsDeletedFalse(),
                "cinemas", cinemaRepository.countByIsActiveTrue(),
                "upcomingShowtimes", showtimeRepository.countActiveStartingFrom(now),
                "activePromotions", promotionRepository.findActivePromotions(now).size()
        ));
        context.put("movies", movies);
        context.put("cinemas", cinemas);
        context.put("upcomingShowtimes", showtimes);
        context.put("activePromotions", promotions);
        context.put("latestNews", news);
        return context;
    }
}
