package com.theBarber.TheBarber.Client.DTO;

import com.theBarber.TheBarber.Client.model.Client;

import java.util.UUID;

public record ClientResponse(
        UUID id, String name, String email, String phone
) {

}
