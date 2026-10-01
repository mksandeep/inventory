package org.sam.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.sam.inventory.entity.User;
import org.sam.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.error("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });

        log.info("User found: {}", email);
        log.debug("User details - Email: {}, Full Name: {}, Active: {}, Roles: {}",
            user.getEmail(), user.getFullName(), user.getActive(), user.getRoles());

        if (!user.getActive()) {
            log.warn("User account is inactive: {}", email);
            throw new UsernameNotFoundException("User account is inactive");
        }

        log.debug("Returning UserDetails for: {}", email);
        return user;
    }
}

