package com.theBarber.TheBarber.Barber.repository;

import com.theBarber.TheBarber.Barber.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface BarberRepository extends JpaRepository<Barber, UUID> {
}
