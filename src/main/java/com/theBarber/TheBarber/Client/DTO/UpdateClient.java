package com.theBarber.TheBarber.Client.DTO;

import java.util.UUID;

public record UpdateClient(

        UUID id, String name, String email, String phone
) {
}
