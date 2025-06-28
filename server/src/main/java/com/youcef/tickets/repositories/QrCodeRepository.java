package com.youcef.tickets.repositories;

import com.youcef.tickets.domain.entities.QrCode;
import com.youcef.tickets.domain.entities.QrCodeStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    Optional<QrCode> findByTicketIdAndTicketPurchaserId(UUID ticketId, UUID purchaserId);
    Optional<QrCode> findByIdAndStatus(UUID id, QrCodeStatusEnum status);

}
