package com.theBarber.TheBarber.Client.model;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.DTO.AppointmentRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;


}
