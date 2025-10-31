package org.lumbi.ejercicio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lumbi.ejercicio.domain.BookDomain;
import org.lumbi.ejercicio.repository.BookRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks_returnsListFromRepository() {
        BookDomain b1 = mock(BookDomain.class);
        BookDomain b2 = mock(BookDomain.class);

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BookDomain> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(b1, result.get(0));
        assertSame(b2, result.get(1));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_whenFound_returnsOptionalWithBook() {
        Long id = 5L;
        BookDomain book = mock(BookDomain.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<BookDomain> result = bookService.getBookById(id);

        assertTrue(result.isPresent());
        assertSame(book, result.get());
        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    void saveBook_delegatesToRepository_andReturnsSaved() {
        BookDomain toSave = new BookDomain();
        toSave.setISBN("ISBN-123");
        toSave.setBookName("Mi Libro");
        toSave.setPublishYear(2020);

        ArgumentCaptor<BookDomain> captor = ArgumentCaptor.forClass(BookDomain.class);

        BookDomain saved = mock(BookDomain.class);
        when(bookRepository.save(captor.capture())).thenReturn(saved);

        BookDomain result = bookService.saveBook(toSave);

        assertSame(saved, result);
        BookDomain captured = captor.getValue();
        assertEquals("ISBN-123", captured.getISBN());
        assertEquals("Mi Libro", captured.getBookName());
        assertEquals(2020, captured.getPublishYear());
        verify(bookRepository, times(1)).save(any(BookDomain.class));
    }

    @Test
    void updateBook_whenExists_updatesFieldsAndSaves() {
        Long id = 10L;
        BookDomain existing = new BookDomain();
        existing.setISBN("VIEJO-ISBN");
        existing.setBookName("Old Name");
        existing.setPublishYear(1999);

        BookDomain details = new BookDomain();
        details.setISBN("NUEVO-ISBN");
        details.setBookName("Nombre Nuevo");
        details.setPublishYear(2023);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Optional<BookDomain> updatedOpt = bookService.updateBook(id, details);

        assertTrue(updatedOpt.isPresent());
        BookDomain updated = updatedOpt.get();
        assertEquals("NUEVO-ISBN", updated.getISBN());
        assertEquals("Nombre Nuevo", updated.getBookName());
        assertEquals(2023, updated.getPublishYear());
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).save(existing);
    }

    @Test
    void updateBook_whenNotFound_returnsEmpty() {
        Long id = 99L;
        BookDomain details = new BookDomain();
        details.setISBN("X");
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<BookDomain> updatedOpt = bookService.updateBook(id, details);

        assertTrue(updatedOpt.isEmpty());
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_whenExists_deletesAndReturnsTrue() {
        Long id = 7L;
        BookDomain book = mock(BookDomain.class);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        boolean result = bookService.deleteBook(id);

        assertTrue(result);
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBook_whenNotFound_returnsFalse() {
        Long id = 777L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = bookService.deleteBook(id);

        assertFalse(result);
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, never()).delete(any());
    }
}