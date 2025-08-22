package com.theBarber.TheBarber.Barber.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.theBarber.TheBarber.Barber.DTO.BarberRequest;
import com.theBarber.TheBarber.Barber.DTO.UpdateBarber;
import com.theBarber.TheBarber.Client.model.Appointment;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "appointments")
public class Barber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    private String cpf;
    private String phone;
    @Embedded
    @Valid
    private Address address;
    @OneToMany(mappedBy = "barber")
    @JsonIgnore
    private List<Appointment> appointments = new ArrayList<>();
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    public Barber(@Valid BarberRequest request, String encryptedPassword) {
        this.id =request.id();
        this.name = request.name();
        this.password = encryptedPassword;
        this.email = request.email();
        this.cpf = request.cpf();
        this.phone = request.phone();
        this.role = request.role() != null ? request.role() : Role.BARBEIRO;
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
