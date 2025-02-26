package com.theBarber.TheBarber.Barber.model;

import com.theBarber.TheBarber.Barber.DTO.BarberRequest;
import com.theBarber.TheBarber.Client.model.Client;
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
    private StatusBarber status;
    private Addres addres;
    @OneToMany(mappedBy = "barber")
    private List<Appointment> appointments = new ArrayList<>();

    public Barber(@Valid BarberRequest request) {
        this.id =request.id();
        this.name = request.name();
        this.email = request.email();
        this.cpf = request.cpf();
        this.phone = request.phone();
        this.addres = getAddres();
    }
}
