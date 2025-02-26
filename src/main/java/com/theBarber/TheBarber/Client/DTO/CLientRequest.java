package com.theBarber.TheBarber.Client.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CLientRequest(

        UUID id,
        @NotBlank
        String name,
        @NotBlank
        @Email
        @Column(unique = true, nullable = false)
        String email,
        @NotNull
        String phone
) {
}
