package com.theBarber.TheBarber.TypeServices.DTO;

import java.math.BigDecimal;

public record UpdateServiceTypes(

        String name,
        BigDecimal price
) {
}
