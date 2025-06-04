package com.theBarber.TheBarber.Barber.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Valid
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @NotBlank(message = "Rua é obrigatória")
    private String street;

    @NotBlank(message = "Bairro é obrigatório")
    private String district;

    @NotBlank(message = "Número é obrigatório")
    private String number;

    @NotBlank(message = "Cidade é obrigatória")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    private String state;

}
