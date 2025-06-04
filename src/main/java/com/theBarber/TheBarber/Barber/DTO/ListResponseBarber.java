package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Barber;

import java.util.UUID;

public record ListResponseBarber(
        UUID id,
        String name,
        String email,
        String phone
) {
    public static ListResponseBarber list(Barber barbers) {
        return new ListResponseBarber(barbers.getId(), barbers.getName(), barbers.getEmail(),
                barbers.getPhone());
    }
}
