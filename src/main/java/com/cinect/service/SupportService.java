package com.cinect.service;

import com.cinect.dto.request.ContactFormRequest;
import com.cinect.entity.SupportTicket;
import com.cinect.repository.BookingRepository;
import com.cinect.repository.SupportTicketRepository;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

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
}
