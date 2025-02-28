package com.theBarber.TheBarber.Barber.model;

import com.theBarber.TheBarber.Barber.DTO.BarberRequest;
import com.theBarber.TheBarber.Barber.DTO.UpdateBarber;
import com.theBarber.TheBarber.Client.model.Appointment;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Barber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    @Enumerated
    private StatusBarber status = StatusBarber.OFFLINE;;
    @Embedded
    @Valid
    private Address address;
    @OneToMany(mappedBy = "barber")
    private List<Appointment> appointments = new ArrayList<>();

    public Barber(@Valid BarberRequest request) {
        this.id =request.id();
        this.name = request.name();
        this.email = request.email();
        this.cpf = request.cpf();
        this.phone = request.phone();
        this.status = request.status() != null ? request.status() : StatusBarber.OFFLINE;
        if (request.address() != null) {
            this.address = new Address(
                    request.address().getStreet(),
                    request.address().getDistrict(),
                    request.address().getNumber(),
                    request.address().getCity(),
                    request.address().getState()
            );
        }
    }

    public Barber(UpdateBarber barber) {
        this.name =barber.name();
        this.email= barber.email();
        this.cpf = barber.cpf();
        this.phone = barber.phone();
        this.address = barber.address();
    }
}
