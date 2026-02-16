package com.cinect.repository;

import com.cinect.entity.Payment;
import com.cinect.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByBooking_Id(UUID bookingId);
    List<Payment> findByStatus(PaymentStatus status);
}
