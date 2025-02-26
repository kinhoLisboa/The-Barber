package com.theBarber.TheBarber.Client.DTO;

import com.theBarber.TheBarber.Client.model.Client;

import java.util.UUID;

public record ListResponseClient(

        UUID id, String name, String phone
) {

    public static ListResponseClient list(Client clients) {
        return new ListResponseClient(clients.getId(), clients.getName(), clients.getPhone());
    }

   
}
