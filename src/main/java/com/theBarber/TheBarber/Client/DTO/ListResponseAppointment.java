package com.theBarber.TheBarber.Client.DTO;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListResponseAppointment(

        UUID id,
        LocalDateTime appointmentTime,
        AppointmentStatus status,
        String barberName,
        String clientName
) {
    public ListResponseAppointment(Appointment appointment) {
        this(   appointment.getId(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getBarber().getName(),
                appointment.getClient().getName()
        );
    }
}
