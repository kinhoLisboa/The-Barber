package com.theBarber.TheBarber_notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentEvent(
        UUID eventId,
        String type,
        UUID appointmentId,
        String clientName,
        String clientPhone,
        LocalDateTime appointmentTime

)
{}
