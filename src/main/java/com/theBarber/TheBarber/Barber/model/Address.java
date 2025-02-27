package com.theBarber.TheBarber.Barber.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
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

    private String street;
    private String district;
    private String number;
    private String city;
    private String state;

}
