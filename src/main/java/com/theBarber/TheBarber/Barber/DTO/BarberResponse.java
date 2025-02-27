package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.StatusBarber;

import java.util.UUID;

public record BarberResponse(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        StatusBarber status
        ) {

}
