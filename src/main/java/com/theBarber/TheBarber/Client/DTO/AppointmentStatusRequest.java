package com.theBarber.TheBarber.Client.DTO;

import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusRequest (

        @NotNull
        AppointmentStatus newStatus
){
}
