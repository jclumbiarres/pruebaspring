package org.lumbi.ejercicio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lumbi.ejercicio.domain.UserDomain;
import org.lumbi.ejercicio.repository.UserRepository;
import org.lumbi.ejercicio.security.JwtTokenProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_whenUsernameAvailable_savesUserAndReturnsUser() {
        String username = "newuser";
        String rawPassword = "secret";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        ArgumentCaptor<UserDomain> saveCaptor = ArgumentCaptor.forClass(UserDomain.class);

        UserDomain savedWithId = mock(UserDomain.class);
        when(savedWithId.getId()).thenReturn(1L);
        when(savedWithId.getUsername()).thenReturn(username);

        when(userRepository.save(saveCaptor.capture())).thenReturn(savedWithId);

        Optional<UserDomain> result = userService.registerUser(username, rawPassword);

        assertTrue(result.isPresent(), "Se esperaba que el usuario estuviera presente tras el registro");
        UserDomain returned = result.get();
        assertEquals(1L, returned.getId(), "El id devuelto debe ser 1");
        assertEquals(username, returned.getUsername(), "El nombre de usuario devuelto debe coincidir");

        UserDomain captured = saveCaptor.getValue();
        assertNotNull(captured.getPassword(), "La contraseña guardada no debe ser nula");
        assertNotEquals(rawPassword, captured.getPassword(),
                "La contraseña debe estar codificada y no ser igual a la original");

        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).save(any(UserDomain.class));
    }

    @Test
    void registerUser_whenUsernameExists_returnsEmptyAndDoesNotSave() {
        String username = "existing";
        String rawPassword = "pw";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        Optional<UserDomain> result = userService.registerUser(username, rawPassword);

        assertTrue(result.isEmpty(), "Se esperaba vacío cuando el nombre de usuario ya existe");
        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticateAndGenerateToken_whenCredentialsValid_returnsToken() {
        String username = "user1";
        String rawPassword = "mypw";

        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        String hashed = encoder.encode(rawPassword);

        UserDomain user = mock(UserDomain.class);
        when(user.getId()).thenReturn(42L);
        when(user.getUsername()).thenReturn(username);
        when(user.getPassword()).thenReturn(hashed);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(eq(username), anyMap())).thenReturn("JWT-TOKEN");

        Optional<String> tokenOpt = userService.authenticateAndGenerateToken(username, rawPassword);

        assertTrue(tokenOpt.isPresent(), "Se esperaba token cuando las credenciales son válidas");
        assertEquals("JWT-TOKEN", tokenOpt.get(), "El token devuelto debe coincidir");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtTokenProvider, times(1)).generateToken(eq(username), claimsCaptor.capture());

        Map<String, Object> claims = claimsCaptor.getValue();
        assertTrue(claims.containsKey("userId"), "Las claims deben contener userId");
        assertEquals(42L, ((Number) claims.get("userId")).longValue(), "El userId en las claims debe ser 42");
        assertEquals("USER", claims.get("role"), "El role en las claims debe ser USER");

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void authenticateAndGenerateToken_whenPasswordInvalid_returnsEmpty() {
        String username = "user2";
        String rawPassword = "correct";
        String wrongPasswordAttempt = "wrong";

        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        String hashed = encoder.encode(rawPassword);

        UserDomain user = mock(UserDomain.class);
        when(user.getPassword()).thenReturn(hashed);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<String> tokenOpt = userService.authenticateAndGenerateToken(username, wrongPasswordAttempt);

        assertTrue(tokenOpt.isEmpty(), "Se esperaba vacío cuando la contraseña no coincide");
        verify(jwtTokenProvider, never()).generateToken(anyString(), anyMap());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsername_and_findById_delegateToRepository() {
        UserDomain user = mock(UserDomain.class);
        when(user.getId()).thenReturn(99L);
        when(user.getUsername()).thenReturn("delegateUser");

        when(userRepository.findByUsername("delegateUser")).thenReturn(Optional.of(user));
        when(userRepository.findById(99L)).thenReturn(Optional.of(user));

        Optional<UserDomain> byName = userService.findByUsername("delegateUser");
        Optional<UserDomain> byId = userService.findById(99L);

        assertTrue(byName.isPresent(), "Se esperaba que existiera el usuario por nombre");
        assertEquals("delegateUser", byName.get().getUsername(), "El nombre de usuario debe coincidir");

        assertTrue(byId.isPresent(), "Se esperaba que existiera el usuario por id");
        assertEquals(99L, byId.get().getId(), "El id debe coincidir");

        verify(userRepository, times(1)).findByUsername("delegateUser");
        verify(userRepository, times(1)).findById(99L);
    }
}