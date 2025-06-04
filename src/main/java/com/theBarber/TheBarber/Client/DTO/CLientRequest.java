package com.theBarber.TheBarber.Client.DTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


import java.util.UUID;

public record CLientRequest(

        UUID id,

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = " Campo senha não pode esta em branco")
        String password,

        @NotBlank(message = "O telefone é obrigatório")
        String phone
) {
}
