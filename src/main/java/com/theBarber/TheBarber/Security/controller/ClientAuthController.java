package com.theBarber.TheBarber.Security.controller;

import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import com.theBarber.TheBarber.Security.DTO.AuthenticationBarberResponse;
import com.theBarber.TheBarber.Security.DTO.ClientLoginRequest;
import com.theBarber.TheBarber.Security.configurations.JwtService;
import com.theBarber.TheBarber.Security.service.ClientUserDetailsService;
import com.theBarber.TheBarber.handle.BarberException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/auth/cliente")
@RequiredArgsConstructor
public class ClientAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;
    private final ClientUserDetailsService clientUserDetailsService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid ClientLoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Client client = clientRepository.findByEmail(request.email())
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,
                        "Cliente não encontrado"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "CLIENT");
        UserDetails user = clientUserDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(claims,user);

        return ResponseEntity.ok(new AuthenticationBarberResponse(token));
    }
}
