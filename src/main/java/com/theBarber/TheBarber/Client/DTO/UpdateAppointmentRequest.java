package com.theBarber.TheBarber.Client.DTO;


import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateAppointmentRequest(


         LocalDateTime newDate,
         UUID newBarberId
) {


}
