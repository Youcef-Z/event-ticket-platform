package com.youcef.tickets.mappers;

import com.youcef.tickets.domain.CreateEventRequest;
import com.youcef.tickets.domain.CreateTicketTypeRequest;
import com.youcef.tickets.domain.dtos.*;
import com.youcef.tickets.domain.entities.Event;
import com.youcef.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventResponseDto(Event event);
}
