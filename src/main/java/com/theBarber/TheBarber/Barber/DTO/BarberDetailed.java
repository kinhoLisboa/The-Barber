package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Addres;

import java.util.UUID;

public record BarberDetailed(

         UUID id,
         String name,
         String email,
         String cpf,
         String phone,
         Addres addres
) {
}
