package org.lumbi.ejercicio.service;

import java.util.List;
import java.util.Optional;

import org.lumbi.ejercicio.domain.BookDomain;
import org.lumbi.ejercicio.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Obtener todos los libros
     * 
     * @return Lista de libros
     */
    public List<BookDomain> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Obtener libro por ID
     * 
     * @param id ID del libro
     * @return Optional con el libro o vacío si no existe
     */
    public Optional<BookDomain> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Guardar un libro (nuevo o editado)
     * 
     * @param book Libro a guardar
     * @return Libro guardado
     */
    public BookDomain saveBook(BookDomain book) {
        return bookRepository.save(book);
    }

    /**
     * Actualizar un libro existente
     * 
     * @param id          ID del libro a actualizar
     * @param bookDetails Detalles del libro a actualizar
     */
    public Optional<BookDomain> updateBook(Long id, BookDomain bookDetails) {
        return bookRepository.findById(id).map(existingBook -> {
            existingBook.setBookName(bookDetails.getBookName());
            existingBook.setISBN(bookDetails.getISBN());
            existingBook.setPublishYear(bookDetails.getPublishYear());
            return bookRepository.save(existingBook);
        });
    }

    /**
     * Eliminar un libro
     * 
     * @param id ID del libro a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean deleteBook(Long id) {
        return bookRepository.findById(id).map(book -> {
            bookRepository.delete(book);
            return true;
        }).orElse(false);
    }
}
