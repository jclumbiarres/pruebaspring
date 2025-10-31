package org.lumbi.ejercicio.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.lumbi.ejercicio.domain.BookDomain;
import org.lumbi.ejercicio.domain.UserDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookRepositoryIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_assignsId_and_findByIdFunciona() {
        UserDomain user = new UserDomain();
        user.setUsername("autor_integ");
        user.setPassword("pwd");
        UserDomain savedUser = userRepository.save(user);

        BookDomain book = new BookDomain();
        book.setISBN("ISBN-INT-001");
        book.setBookName("Libro de integración");
        book.setPublishYear(2021);
        book.setAddedBy(savedUser);

        BookDomain saved = bookRepository.save(book);

        assertNotNull(saved.getId(), "El id debería asignarse automáticamente al guardar (no usar setId)");
        Optional<BookDomain> found = bookRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Se esperaba encontrar el libro guardado por ID");
        assertEquals("ISBN-INT-001", found.get().getISBN(), "El ISBN recuperado debe coincidir con el guardado");
    }

    @Test
    void findByIsbnContainingIgnoreCase_busquedaParcial_sensibleACasoNo() {
        BookDomain b1 = new BookDomain();
        b1.setISBN("abc-123");
        b1.setBookName("Alpha");
        b1.setPublishYear(2010);
        b1.setAddedBy(createAndSaveUser("u1"));
        bookRepository.save(b1);

        BookDomain b2 = new BookDomain();
        b2.setISBN("XYZ-abc-999");
        b2.setBookName("Beta");
        b2.setPublishYear(2015);
        b2.setAddedBy(createAndSaveUser("u2"));
        bookRepository.save(b2);

        List<BookDomain> results = bookRepository.findByIsbnContainingIgnoreCase("ABC");
        assertFalse(results.isEmpty(), "La búsqueda parcial por ISBN debería devolver al menos un resultado");
        boolean contiene = results.stream()
                .anyMatch(b -> "abc-123".equals(b.getISBN()) || "XYZ-abc-999".equals(b.getISBN()));
        assertTrue(contiene,
                "La lista de resultados debe contener los libros cuyo ISBN contiene 'ABC' sin importar el case");
    }

    @Test
    void findByBooknameContainingIgnoreCase_busquedaParcial_sensibleACasoNo() {
        BookDomain b1 = new BookDomain();
        b1.setISBN("ISBN-A");
        b1.setBookName("El Gran Libro");
        b1.setPublishYear(2000);
        b1.setAddedBy(createAndSaveUser("u3"));
        bookRepository.save(b1);

        BookDomain b2 = new BookDomain();
        b2.setISBN("ISBN-B");
        b2.setBookName("gran viaje");
        b2.setPublishYear(2005);
        b2.setAddedBy(createAndSaveUser("u4"));
        bookRepository.save(b2);

        List<BookDomain> results = bookRepository.findByBooknameContainingIgnoreCase("GRAN");
        assertFalse(results.isEmpty(), "La búsqueda parcial por nombre debe devolver resultados");
        boolean contiene = results.stream()
                .anyMatch(b -> "El Gran Libro".equals(b.getBookName()) || "gran viaje".equals(b.getBookName()));
        assertTrue(contiene,
                "La lista debe contener libros cuyo nombre contiene 'GRAN' ignorando mayúsculas/minúsculas");
    }

    @Test
    void findByAddedBy_devuelveSoloLibrosDelUsuario() {
        UserDomain author = createAndSaveUser("autor_libros");
        UserDomain other = createAndSaveUser("otro_autor");

        BookDomain b1 = new BookDomain();
        b1.setISBN("A-1");
        b1.setBookName("Libro A1");
        b1.setPublishYear(2018);
        b1.setAddedBy(author);
        bookRepository.save(b1);

        BookDomain b2 = new BookDomain();
        b2.setISBN("A-2");
        b2.setBookName("Libro A2");
        b2.setPublishYear(2019);
        b2.setAddedBy(author);
        bookRepository.save(b2);

        BookDomain b3 = new BookDomain();
        b3.setISBN("B-1");
        b3.setBookName("Libro B1");
        b3.setPublishYear(2020);
        b3.setAddedBy(other);
        bookRepository.save(b3);

        List<BookDomain> authorBooks = bookRepository.findByAddedBy(author);
        assertEquals(2, authorBooks.size(), "El autor debe tener exactamente 2 libros asociados");
        boolean allAddedByAuthor = authorBooks.stream().allMatch(b -> author.getId().equals(b.getAddedBy().getId()));
        assertTrue(allAddedByAuthor, "Todos los libros devueltos deben pertenecer al autor buscado");
    }

    // Helper para crear y persistir usuarios en tests
    private UserDomain createAndSaveUser(String username) {
        UserDomain u = new UserDomain();
        u.setUsername(username);
        u.setPassword("x");
        return userRepository.save(u);
    }
}