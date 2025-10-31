package org.lumbi.ejercicio.service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.lumbi.ejercicio.domain.UserDomain;
import org.lumbi.ejercicio.repository.UserRepository;
import org.lumbi.ejercicio.security.JwtTokenProvider;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registra un nuevo usuario si el nombre de usuario no está ya en uso.
     * Devuelve un Optional con el usuario registrado o vacío si el nombre de
     * usuario ya existe
     * 
     * @param username Nombre de usuario del nuevo usuario
     * 
     * @param password Contraseña del nuevo usuario
     */
    public Optional<UserDomain> registerUser(String username, String password) {
        return Optional.of(username)
                .filter(this::isUsernameAvailable)
                .map(encodePassword(password))
                .map(this::saveUser);
    }

    /**
     * Autentica a un usuario y genera un token JWT si las credenciales son válidas.
     * Devuelve un Optional con el token o vacío si la autenticación falla.
     * 
     * @param username Nombre de usuario del usuario
     * 
     * @param password Contraseña del usuario
     */
    public Optional<String> authenticateAndGenerateToken(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(this::generateTokenForUser);
    }

    private String generateTokenForUser(UserDomain user) {
        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "role", "USER");
        return jwtTokenProvider.generateToken(user.getUsername(), claims);
    }

    private boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    private Function<String, UserDomain> encodePassword(String password) {
        return username -> {
            UserDomain user = new UserDomain();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            return user;
        };
    }

    private UserDomain saveUser(UserDomain user) {
        return userRepository.save(user);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * Devuelve un Optional con el usuario o vacío si no se encuentra.
     * 
     * @param username Nombre de usuario a buscar
     * @return Optional con el usuario o vacío si no se encuentra
     */
    public Optional<UserDomain> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Busca un usuario por su ID.
     * Devuelve un Optional con el usuario o vacío si no se encuentra.
     * 
     * @param id ID del usuario a buscar
     * @return Optional con el usuario o vacío si no se encuentra
     */
    public Optional<UserDomain> findById(Long id) {
        return userRepository.findById(id);
    }
}