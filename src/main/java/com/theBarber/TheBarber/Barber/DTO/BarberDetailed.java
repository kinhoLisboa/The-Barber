package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Address;

import java.util.UUID;

public record BarberDetailed(

         UUID id,
         String name,
         String email,
         String cpf,
         String phone,
         AddressResponse addres
) {
    public BarberDetailed(UUID id, String name, String email, String cpf, String phone, Address address) {
        this(id, name, email, cpf, phone, new AddressResponse(
                address.getStreet(), address.getDistrict(), address.getNumber(), address.getCity(), address.getState()));
    }

}
