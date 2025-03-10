package com.theBarber.TheBarber.Client.repository;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.model.Appointment;
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

//    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.barber.id = :barberId AND " +
//            "((a.appointmentTime BETWEEN :startTime AND :endTime) OR (a.endTime BETWEEN :startTime AND :endTime))")
//    boolean existsByBarberAndOverlappingTime(@Param("barberId") UUID barberId,
//                                             @Param("startTime") LocalDateTime startTime,
//                                             @Param("endTime") LocalDateTime endTime);
//
//    @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId ORDER BY a.appointmentTime DESC")
//    Optional<Appointment> findLastAppointmentByBarber(@Param("barberId") UUID barberId);


}
