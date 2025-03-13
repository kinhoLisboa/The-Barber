package com.theBarber.TheBarber.TypeServices.DTO;

import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;

import java.math.BigDecimal;

public record ListResponseTypeService(

        String name,
        BigDecimal price
) {
    public static ListResponseTypeService list(ServiceTypes types) {
        return new ListResponseTypeService(types.getName(), types.getPrice());
    }

}
