package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Addres;

public record UpdateBarber(

        String email,
        String phone,
        Addres addres
) {
}
