package com.theBarber.TheBarber.Client.model;

import com.theBarber.TheBarber.Client.DTO.CLientRequest;
import com.theBarber.TheBarber.Client.DTO.UpdateClient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String email;
    private String phone;
    @OneToMany(mappedBy = "client")
    private List<Appointment> appointments = new ArrayList<>();

    public Client(CLientRequest request) {
        this.id = request.id();
        this.name = request.name();
        this.email = request.email();
        this.phone = request.phone();
    }


    public Client(UpdateClient client) {
        this.email = client.email();
        this.phone = client.phone();
    }
}
