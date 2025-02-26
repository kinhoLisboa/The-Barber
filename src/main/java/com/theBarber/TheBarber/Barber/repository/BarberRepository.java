package com.theBarber.TheBarber.Barber.repository;

import com.theBarber.TheBarber.Barber.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BarberRepository extends JpaRepository<Barber, UUID> {
}
