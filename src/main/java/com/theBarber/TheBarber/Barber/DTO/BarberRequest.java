package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Address;
import com.theBarber.TheBarber.Barber.model.StatusBarber;

import java.util.UUID;

public record BarberRequest(
         UUID id,
         String name,
         String username,
         String password,
         String email,
         String cpf,
         String phone,
         StatusBarber status,
         Address address

) {
}
