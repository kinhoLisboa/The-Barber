package com.theBarber.TheBarber.Barber.model;

import jakarta.persistence.*;
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
}
