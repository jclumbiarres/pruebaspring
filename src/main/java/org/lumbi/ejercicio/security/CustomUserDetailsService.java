package org.lumbi.ejercicio.security;

import org.lumbi.ejercicio.domain.UserDomain;
import org.lumbi.ejercicio.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su nombre de usuario para autenticación.
     * 
     * @param username Nombre de usuario del usuario
     * @return UserDetails los detalles del usuario para autenticación
     * @throws UsernameNotFoundException si el usuario no se encuentra
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDomain userDomain = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));

        return User.builder()
                .username(userDomain.getUsername())
                .password(userDomain.getPassword())
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Carga un UserDomain por su nombre de usuario.
     * 
     * @param username
     * @return UserDomain el dominio del usuario
     * @throws UsernameNotFoundException
     */
    public UserDomain loadUserDomainByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }
}