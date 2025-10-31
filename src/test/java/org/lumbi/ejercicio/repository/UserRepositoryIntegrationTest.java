package org.lumbi.ejercicio.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.lumbi.ejercicio.domain.UserDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_assignsId_and_findByIdFunciona() {
        UserDomain user = new UserDomain();
        user.setUsername("usuario_integ_1");
        user.setPassword("pass");

        UserDomain saved = userRepository.save(user);

        assertNotNull(saved.getId(), "El id debería asignarse automáticamente al guardar (no usar setId)");
        Optional<UserDomain> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Se esperaba encontrar el usuario guardado por ID");
        assertEquals("usuario_integ_1", found.get().getUsername(),
                "El username recuperado debe coincidir con el guardado");
    }

    @Test
    void findByUsername_and_existsByUsername_funcionan() {
        String username = "usuario_existente";
        UserDomain user = new UserDomain();
        user.setUsername(username);
        user.setPassword("secret");
        userRepository.save(user);

        Optional<UserDomain> byUsername = userRepository.findByUsername(username);
        assertTrue(byUsername.isPresent(), "findByUsername debe devolver un Optional con el usuario existente");

        boolean exists = userRepository.existsByUsername(username);
        assertTrue(exists, "existsByUsername debe devolver true para un username ya guardado");
    }

    @Test
    void findByUsernameContainingIgnoreCase_busquedaParcial_sensibleACasoNo() {
        UserDomain u1 = new UserDomain();
        u1.setUsername("juanPerez");
        u1.setPassword("x");
        userRepository.save(u1);

        UserDomain u2 = new UserDomain();
        u2.setUsername("UsuarioPrueba");
        u2.setPassword("x");
        userRepository.save(u2);

        List<UserDomain> results = userRepository.findByUsernameContainingIgnoreCase("usuario");
        assertFalse(results.isEmpty(), "La búsqueda parcial debería devolver al menos un resultado");
        boolean contieneUsuarioPrueba = results.stream().anyMatch(u -> "UsuarioPrueba".equals(u.getUsername()));
        assertTrue(contieneUsuarioPrueba,
                "La lista de resultados debe contener 'UsuarioPrueba' independientemente del case");
    }
}
