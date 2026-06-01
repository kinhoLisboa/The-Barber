package com.theBarber.TheBarber.Client.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AppointmentRequest(

         @NotNull(message = "ID do barbeiro é obrigatório")
         UUID barberId,
         @NotNull(message = "ID do cliente é obrigatório")
         UUID clientId,
         @NotNull(message = "Data e hora são obrigatórias")
         LocalDateTime appointmentTime,
         List<UUID> servicesId
) {
}
