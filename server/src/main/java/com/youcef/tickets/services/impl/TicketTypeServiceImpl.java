package com.youcef.tickets.services.impl;

import com.youcef.tickets.domain.entities.Ticket;
import com.youcef.tickets.domain.entities.TicketStatusEnum;
import com.youcef.tickets.domain.entities.TicketType;
import com.youcef.tickets.domain.entities.User;
import com.youcef.tickets.exceptions.TicketSoldOutException;
import com.youcef.tickets.exceptions.TicketTypeNotFoundException;
import com.youcef.tickets.exceptions.UserNotFoundException;
import com.youcef.tickets.repositories.TicketRepository;
import com.youcef.tickets.repositories.TicketTypeRepository;
import com.youcef.tickets.repositories.UserRepository;
import com.youcef.tickets.services.QrCodeService;
import com.youcef.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User with id " + userId + " not found"));

        // We need to lock the ticket type to avoid race conditions
        // for when two users try to purchase the same ticket type at the same time
        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() ->
                new TicketTypeNotFoundException("Ticket type with id " + ticketTypeId + " not found"));

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketTypeId);
        Integer totalAvailable = ticketType.getTotalAvailable();
        if (purchasedTickets + 1 > totalAvailable) {
            throw new TicketSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);
        return ticketRepository.save(savedTicket);
    }
}
