package com.theBarber.TheBarber.TypeServices.DTO;

import java.math.BigDecimal;

public record ServiceTypeRequest(

        String name,
        BigDecimal price,
        String description
) {
}
