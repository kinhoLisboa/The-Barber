package com.theBarber.TheBarber.Client.DTO;

import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        LocalDateTime appointmentTime,
        AppointmentStatus status,
        List<ServiceTypeResponse > services
) {

    public AppointmentResponse(UUID id, LocalDateTime appointmentTime, AppointmentStatus status) {
        this(id, appointmentTime, status, List.of());
    }

}
