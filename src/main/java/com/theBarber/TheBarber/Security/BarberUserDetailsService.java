package com.theBarber.TheBarber.Security;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BarberUserDetailsService implements UserDetailsService {

    private BarberRepository barberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Barber barber = barberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Barber not found"));

        return new User(barber.getEmail(), barber.getPassword(), List.of());
    }
}
