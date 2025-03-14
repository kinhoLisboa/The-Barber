package com.theBarber.TheBarber.TypeServices.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceTypeResponse(
        UUID id,
        String name,
        BigDecimal price

) {
}
