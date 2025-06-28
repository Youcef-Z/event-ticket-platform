package com.youcef.tickets.domain.dtos;

import com.youcef.tickets.domain.entities.TicketValidationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequestDto {
    // This id could represent the ticketId or the qrCodeId
    private UUID id;
    private TicketValidationMethod method;
}
