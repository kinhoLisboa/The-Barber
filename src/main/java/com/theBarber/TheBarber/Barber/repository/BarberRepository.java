package com.theBarber.TheBarber.Barber.repository;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.model.Role;
import com.theBarber.TheBarber.Client.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface BarberRepository extends JpaRepository<Barber, UUID> {

      Optional<Barber> findByName(String name);

    Optional<Barber> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);
}
