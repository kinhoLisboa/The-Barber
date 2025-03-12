package com.theBarber.TheBarber.TypeServices.DTO;

import java.math.BigDecimal;

public record ServiceTypeResponse(

        String name,
        BigDecimal price

) {
}
