package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Addres;
import com.theBarber.TheBarber.Barber.model.StatusBarber;
import com.theBarber.TheBarber.Client.model.Client;

import java.util.UUID;

public record BarberRequest(
         UUID id,
         String name,
         String email,
         String cpf,
         String phone,
         Addres addres,
         Client client
) {
}
