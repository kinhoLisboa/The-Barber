package com.theBarber.TheBarber.Client.DTO;

import com.theBarber.TheBarber.Barber.DTO.BarberResponse;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        LocalDateTime appointmentTime,
        AppointmentStatus status
) {
}
