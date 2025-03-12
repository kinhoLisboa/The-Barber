package com.theBarber.TheBarber.Client.repository;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByBarberAndAppointmentTime(Barber barber, LocalDateTime appointmentTime);
    List<Appointment> findByBarberId(UUID barberId);

    // Verificar se o novo agendamento se sobrepõe ao agendamento anterior
    @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId AND " +
            "(a.appointmentTime BETWEEN :startTime AND :endTime)")
    List<Appointment> findOverlappingAppointments(@Param("barberId") UUID barberId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT a FROM Appointment a
    JOIN FETCH a.barber b
    JOIN FETCH a.client c
""")
    Page<Appointment> findAllWithBarberAndClient(Pageable pageable);

    Optional<Appointment> findByClientId(UUID clientId);
}
