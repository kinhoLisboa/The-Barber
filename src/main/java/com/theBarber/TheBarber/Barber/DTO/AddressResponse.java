package com.theBarber.TheBarber.Barber.DTO;

public record AddressResponse(

        String street,
        String district,
        String number,
        String city,
        String state
) {
}
