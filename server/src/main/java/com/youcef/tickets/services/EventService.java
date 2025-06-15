package com.youcef.tickets.services;

import com.youcef.tickets.domain.CreateEventRequest;
import com.youcef.tickets.domain.entities.Event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import com.youcef.tickets.domain.UpdateEventRequest;

public interface EventService {

    Event createEvent(UUID organizerId, CreateEventRequest event);
    Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable);
    Optional<Event> getEventForOrganizer(UUID organizerId, UUID id);
    Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest event);
}
