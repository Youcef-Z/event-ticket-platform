package com.youcef.tickets.services;

import com.youcef.tickets.domain.CreateEventRequest;
import com.youcef.tickets.domain.entities.Event;

import java.util.UUID;

public interface EventService {

    Event createEvent(UUID organizerId, CreateEventRequest event);
}
