package com.theBarber.TheBarber.Security.controller;

import com.theBarber.TheBarber.Security.DTO.AuthenticationBarberRequest;
import com.theBarber.TheBarber.Security.DTO.AuthenticationBarberResponse;
import com.theBarber.TheBarber.Security.cofigurations.JwtService;
import com.theBarber.TheBarber.Security.service.BarberUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class BarberAuthController {


    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private BarberUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationBarberResponse> login(@RequestBody AuthenticationBarberRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthenticationBarberResponse(token));
    }

}
