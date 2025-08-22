package com.theBarber.TheBarber.Security.controller;

import com.theBarber.TheBarber.Security.DTO.AuthenticationBarberRequest;
import com.theBarber.TheBarber.Security.DTO.AuthenticationBarberResponse;
import com.theBarber.TheBarber.Security.cofigurations.JwtService;
import com.theBarber.TheBarber.Security.service.BarberUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class BarberAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BarberUserDetailsService userDetailsService;



    @PostMapping("/login")
    public ResponseEntity<AuthenticationBarberResponse> login(@RequestBody AuthenticationBarberRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "BARBEIRO");

        UserDetails user = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(claims,user);

        return ResponseEntity.ok(new AuthenticationBarberResponse(token));
    }

}
