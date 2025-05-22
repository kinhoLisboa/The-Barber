package com.theBarber.TheBarber.Security;

public record AuthenticationRequest(
        String username,
        String password) {
}
