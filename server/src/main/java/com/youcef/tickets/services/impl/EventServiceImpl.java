package com.youcef.tickets.services.impl;

import com.youcef.tickets.domain.CreateEventRequest;
import com.youcef.tickets.domain.entities.Event;
import com.youcef.tickets.domain.entities.TicketType;
import com.youcef.tickets.domain.entities.User;
import com.youcef.tickets.exceptions.UserNotFoundException;
import com.youcef.tickets.repositories.EventRepository;
import com.youcef.tickets.repositories.UserRepository;
import com.youcef.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest event) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(
                    String.format("User with id %s not found", organizerId)
                ));

        Event eventToCreate = new Event();

        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(
                ticketTypeRequest -> {
                    TicketType ticketTypeToCreate = new TicketType();
                    ticketTypeToCreate.setName(ticketTypeRequest.getName());
                    ticketTypeToCreate.setPrice(ticketTypeRequest.getPrice());
                    ticketTypeToCreate.setDescription(ticketTypeRequest.getDescription());
                    ticketTypeToCreate.setTotalAvailable(ticketTypeRequest.getTotalAvailable());
                    ticketTypeToCreate.setEvent(eventToCreate);
                    return ticketTypeToCreate;
                }
        ).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStart(event.getStart());
        eventToCreate.setEnd(event.getEnd());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSalesStart(event.getSalesStart());
        eventToCreate.setSalesEnd(event.getSalesEnd());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setOrganizer(organizer);
        eventToCreate.setTicketTypes(ticketTypesToCreate);

        return eventRepository.save(eventToCreate);
    }
}
