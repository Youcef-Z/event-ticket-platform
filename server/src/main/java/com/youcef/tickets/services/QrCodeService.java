package com.youcef.tickets.services;


import com.youcef.tickets.domain.entities.QrCode;
import com.youcef.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
