package com.theBarber.TheBarber.TypeServices.model;

import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeRequest;
import com.theBarber.TheBarber.TypeServices.DTO.UpdateServiceTypes;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceTypes {

    private UUID id;
    private String name;
    private BigDecimal price;
    private String description;


    public ServiceTypes(ServiceTypeRequest newService) {
        this.name = newService.name();
        this.price = newService.price();
        this.description = newService.description();
    }

    public ServiceTypes(UpdateServiceTypes types) {
        this.name= types.name();
        this.price = types.price();
    }
}
