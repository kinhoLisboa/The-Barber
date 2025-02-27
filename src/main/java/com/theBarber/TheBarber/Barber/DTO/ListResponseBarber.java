package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.model.StatusBarber;

import java.util.UUID;

public record ListResponseBarber(
        UUID id,
        String name,
        String email,
        String phone,
        StatusBarber status
) {
    public static ListResponseBarber list(Barber barbers) {
        return new ListResponseBarber(barbers.getId(), barbers.getName(), barbers.getEmail(),
                barbers.getPhone(),barbers.getStatus());
    }
}
