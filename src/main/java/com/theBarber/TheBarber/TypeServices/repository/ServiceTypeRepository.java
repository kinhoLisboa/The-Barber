package com.theBarber.TheBarber.TypeServices.repository;

import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceTypes, UUID> {
}
