package com.youcef.tickets.services.impl;

import com.youcef.tickets.domain.entities.*;
import com.youcef.tickets.exceptions.QrCodeNotFoundException;
import com.youcef.tickets.exceptions.TicketNotFoundException;
import com.youcef.tickets.repositories.QrCodeRepository;
import com.youcef.tickets.repositories.TicketRepository;
import com.youcef.tickets.repositories.TicketValidationRepository;
import com.youcef.tickets.services.TicketValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketValidationServiceImpl implements TicketValidationService {

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final TicketRepository ticketRepository;

    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(
                        String.format("QrCode with id %s not found", qrCodeId)
                ));
        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket, TicketValidationMethod.QR_SCAN);
    }

    private TicketValidation validateTicket(Ticket ticket, TicketValidationMethod ticketValidationMethod) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(ticketValidationMethod);

        // We determine the status by checking if the ticket has already been validated before
        // if it has been validated, we set the status to invalid
        // if it hasn't and this is the first validation, we set the status to valid
        TicketValidationStatusEnum ticketValidationStatus = ticket.getValidations().stream()
                .filter(v -> v.getStatus().equals(TicketValidationStatusEnum.VALID))
                .findFirst()
                .map(v -> TicketValidationStatusEnum.INVALID)
                .orElse(TicketValidationStatusEnum.VALID);

        ticketValidation.setStatus(ticketValidationStatus);

        return ticketValidationRepository.save(ticketValidation);
    }

    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(
                String.format("Ticket with id %s not found", ticketId)
        ));

        return validateTicket(ticket, TicketValidationMethod.MANUAL);
    }
}
