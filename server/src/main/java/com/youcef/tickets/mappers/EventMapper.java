package com.youcef.tickets.mappers;

import com.youcef.tickets.domain.CreateEventRequest;
import com.youcef.tickets.domain.CreateTicketTypeRequest;
import com.youcef.tickets.domain.dtos.CreateEventRequestDto;
import com.youcef.tickets.domain.dtos.CreateEventResponseDto;
import com.youcef.tickets.domain.dtos.CreateTicketTypeRequestDto;
import com.youcef.tickets.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

}
