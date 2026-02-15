package com.cinect.service;

import com.cinect.dto.request.InitiatePaymentRequest;
import com.cinect.dto.response.PaymentResponse;
import com.cinect.entity.Booking;
import com.cinect.entity.Payment;
import com.cinect.entity.enums.PaymentStatus;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.BookingRepository;
import com.cinect.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Transactional
    public PaymentResponse initiatePayment(UUID userId, InitiatePaymentRequest req) {
        var booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }
        if (booking.getStatus() != com.cinect.entity.enums.BookingStatus.PENDING) {
            throw new BadRequestException("Booking is not pending");
        }
        var transactionId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        var paymentUrl = "/payment/simulated?transactionId=" + transactionId + "&amount=" + booking.getFinalAmount();
        var payment = Payment.builder()
                .booking(booking)
                .method(req.getMethod())
                .amount(booking.getFinalAmount())
                .status(PaymentStatus.PENDING)
                .transactionId(transactionId)
                .paymentUrl(paymentUrl)
                .build();
        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }

    @Transactional
    public void handleCallback(String transactionId, boolean success) {
        var payment = paymentRepository.findByTransactionId(transactionId)
                .orElse(null);
        if (payment == null) return;
        if (payment.getStatus() == PaymentStatus.PAID) return;
        payment.setStatus(success ? PaymentStatus.PAID : PaymentStatus.FAILED);
        payment.setPaidAt(success ? java.time.Instant.now() : null);
        paymentRepository.save(payment);
        if (success) {
            bookingService.confirmBooking(payment.getBooking().getId(), payment.getBooking().getUser().getId());
        }
    }

    public PaymentResponse getPaymentStatus(UUID id, UUID userId) {
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!payment.getBooking().getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .bookingId(p.getBooking().getId())
                .method(p.getMethod())
                .amount(p.getAmount())
                .status(p.getStatus())
                .transactionId(p.getTransactionId())
                .paymentUrl(p.getPaymentUrl())
                .errorReason(p.getErrorReason())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
