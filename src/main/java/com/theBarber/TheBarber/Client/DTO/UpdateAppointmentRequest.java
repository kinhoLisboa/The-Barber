package com.theBarber.TheBarber.Client.DTO;


import java.time.LocalDateTime;

public record UpdateAppointmentRequest(


         LocalDateTime newDate,
         String newBarberName
) {


}
