package com.youcef.tickets.services.impl;

import com.youcef.tickets.domain.CreateEventRequest;
import com.youcef.tickets.domain.UpdateEventRequest;
import com.youcef.tickets.domain.UpdateTicketTypeRequest;
import com.youcef.tickets.domain.entities.Event;
import com.youcef.tickets.domain.entities.EventStatusEnum;
import com.youcef.tickets.domain.entities.TicketType;
import com.youcef.tickets.domain.entities.User;
import com.youcef.tickets.exceptions.EventNotFoundException;
import com.youcef.tickets.exceptions.EventUpdateException;
import com.youcef.tickets.exceptions.TicketTypeNotFoundException;
import com.youcef.tickets.exceptions.UserNotFoundException;
import com.youcef.tickets.repositories.EventRepository;
import com.youcef.tickets.repositories.UserRepository;
import com.youcef.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
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

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest event) {
        if (event.getId() == null) {
            throw new EventUpdateException("Event id must be present");
        }

        if (!eventId.equals(event.getId())) {
            throw new EventUpdateException("Event id must be the same as the one in the request");
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> new EventNotFoundException("Event with Id " + eventId + " not found"));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(ticketType -> !requestTicketTypeIds.contains(ticketType.getId()));

        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for (UpdateTicketTypeRequest ticketTypeRequest : event.getTicketTypes()) {
            if (ticketTypeRequest.getId() == null) {
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketTypeRequest.getName());
                ticketTypeToCreate.setPrice(ticketTypeRequest.getPrice());
                ticketTypeToCreate.setDescription(ticketTypeRequest.getDescription());
                ticketTypeToCreate.setTotalAvailable(ticketTypeRequest.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);
            } else if (existingTicketTypesIndex.containsKey(ticketTypeRequest.getId())) {
                TicketType existingTicketType = existingTicketTypesIndex.get(ticketTypeRequest.getId());
                existingTicketType.setName(ticketTypeRequest.getName());
                existingTicketType.setPrice(ticketTypeRequest.getPrice());
                existingTicketType.setDescription(ticketTypeRequest.getDescription());
                existingTicketType.setTotalAvailable(ticketTypeRequest.getTotalAvailable());
            } else {
                throw new TicketTypeNotFoundException("Ticket type with id " + ticketTypeRequest.getId() + " not found");
            }
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID eventId) {
        getEventForOrganizer(organizerId, eventId).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvent(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }
}
