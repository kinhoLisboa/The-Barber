package com.theBarber.TheBarber.Client.DTO;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.Client;

import java.time.LocalDateTime;

public record UpdateAppointmentResponse(

        String clientName,
        String barberName,
        LocalDateTime appointmentDate
) {

    public UpdateAppointmentResponse(Appointment appointment) {
        this(
                appointment.getClient().getName(),
                appointment.getBarber().getName(),
                appointment.getAppointmentTime()
        );
    }
}
