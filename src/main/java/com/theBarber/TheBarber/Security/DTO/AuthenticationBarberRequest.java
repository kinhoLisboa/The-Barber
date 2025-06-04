package com.theBarber.TheBarber.Security.DTO;

public record AuthenticationBarberRequest(
        String email,
        String password) {
}
