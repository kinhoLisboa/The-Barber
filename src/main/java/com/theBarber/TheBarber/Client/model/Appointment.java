package com.theBarber.TheBarber.Client.model;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import com.theBarber.TheBarber.handle.BarberException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private LocalDateTime endTime;

    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @ManyToMany
    @JoinTable(
            name = "appointment_service",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    private List<ServiceTypes> services = new ArrayList<>();


    public void changeStatus(AppointmentStatus newStatus) {

        if (this.status == AppointmentStatus.CANCELED) {
            throw BarberException.build(HttpStatus.BAD_REQUEST, "Não é possível mudar o " +
                    "status de um agendamento cancelado.");
        }
        switch (newStatus) {
            case CONFIRMED:
                if (this.status != AppointmentStatus.PENDING) {
                    throw BarberException.build(HttpStatus.BAD_REQUEST, "Só é possível confirmar " +
                            "agendamentos pendentes.");
                }
                break;
            case CANCELED:
                if (this.status == AppointmentStatus.FINALIZED) {
                    throw BarberException.build(HttpStatus.BAD_REQUEST, "Não é possível cancelar " +
                            "um agendamento já concluído.");
                }
                break;
            case FINALIZED:
                if (this.status != AppointmentStatus.CONFIRMED) {
                    throw BarberException.build(HttpStatus.BAD_REQUEST, "Só é possível concluir " +
                            "agendamentos confirmados.");
                }
                break;
            case PENDING:
                break;
            default:
                throw BarberException.build(HttpStatus.BAD_REQUEST, "Status desconhecido.");
        }
        this.status = newStatus;
    }



}
