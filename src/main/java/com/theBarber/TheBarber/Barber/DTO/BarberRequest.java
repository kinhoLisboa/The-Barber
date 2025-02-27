package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Address;

import java.util.UUID;

public record BarberRequest(
         UUID id,
         String name,
         String email,
         String cpf,
         String phone,
         Address address

) {
}
