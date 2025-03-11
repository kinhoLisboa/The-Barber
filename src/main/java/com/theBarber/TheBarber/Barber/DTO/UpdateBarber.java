package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Address;


public record UpdateBarber(

        String name,
        String email,
        String cpf,
        String phone,
        Address address
) {
}
