package com.theBarber.TheBarber.Client.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(

         UUID barberId,

         UUID clientId,

         LocalDateTime appointmentTime
) {
}
