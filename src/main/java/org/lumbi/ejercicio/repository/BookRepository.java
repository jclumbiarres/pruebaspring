package org.lumbi.ejercicio.repository;

import org.lumbi.ejercicio.domain.BookDomain;
import org.lumbi.ejercicio.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookDomain, Long> {

    // Buscar libros por ISBN (opcional)
    List<BookDomain> findByIsbnContainingIgnoreCase(String isbn);

    // Buscar libros por nombre
    List<BookDomain> findByBooknameContainingIgnoreCase(String bookname);

    // Obtener todos los libros añadidos por un usuario
    List<BookDomain> findByAddedBy(UserDomain user);
}
