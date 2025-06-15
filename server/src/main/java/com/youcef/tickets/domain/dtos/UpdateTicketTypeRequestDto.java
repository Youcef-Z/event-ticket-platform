package com.youcef.tickets.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketTypeRequestDto {

    private UUID id;

    @NotBlank(message = "Ticket type name is required")
    private String name;

    @NotNull(message = "Ticket type price is required")
    @PositiveOrZero(message = "Ticket type price must be greater than or equal to zero")
    private Double price;

    @Max(value = 300, message = "Ticket type description cannot exceed {value} characters")
    private String description;

    private Integer totalAvailable;
}
