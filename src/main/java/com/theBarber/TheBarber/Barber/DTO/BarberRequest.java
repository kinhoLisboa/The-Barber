package com.theBarber.TheBarber.Barber.DTO;

import com.theBarber.TheBarber.Barber.model.Address;
import com.theBarber.TheBarber.Barber.model.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

public record BarberRequest(
         UUID id,
         @NotBlank(message = "O nome é obrigatório")
         String name,
         @NotBlank(message = "A senha é obrigatória")
         String password,
         @NotBlank(message = "O email é obrigatório")
         @Email(message = "Email inválido")
         String email,
         @NotBlank(message = "O CPF é obrigatório")
         @CPF(message = "CPF inválido")
         String cpf,
         @NotBlank(message = "O telefone é obrigatório")
         String phone,
         Role role,
         @Valid
         Address address


) {
}
