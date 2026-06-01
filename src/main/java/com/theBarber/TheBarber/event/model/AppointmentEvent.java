package com.theBarber.TheBarber.event.model;


import com.theBarber.TheBarber.event.enums.AppointmentEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentEvent(
        UUID eventId,
        AppointmentEventType type,
        UUID appointmentId,
        String clientName,
        String clientPhone,
        LocalDateTime appointmentTime
)  {

}
