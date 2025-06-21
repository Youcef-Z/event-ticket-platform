package com.youcef.tickets.services;


import com.youcef.tickets.domain.entities.QrCode;
import com.youcef.tickets.domain.entities.Ticket;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

}
