package org.lumbi.ejercicio.controller;

import org.lumbi.ejercicio.domain.BookDomain;
import org.lumbi.ejercicio.domain.UserDomain;
import org.lumbi.ejercicio.dto.BookRequestDTO;
import org.lumbi.ejercicio.service.BookService;
import org.lumbi.ejercicio.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "API de gestión de libros")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Obtener todos los libros", description = "Devuelve una lista de todos los libros disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de libros", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDomain.class))),
            @ApiResponse(responseCode = "204", description = "No hay libros disponibles"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<BookDomain>> getAllBooks() {
        List<BookDomain> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Obtener libro por ID", description = "Devuelve un libro según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDomain.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDomain> getBookById(@PathVariable Long id) {
        Optional<BookDomain> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear nuevo libro", description = "Crea un nuevo libro asociado a un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDomain.class))),
            @ApiResponse(responseCode = "400", description = "ID de usuario inválido o datos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<BookDomain> createBook(@Valid @RequestBody BookRequestDTO bookDTO) {
        Optional<UserDomain> userOpt = userService.findById(bookDTO.getAddedById());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BookDomain book = new BookDomain();
        book.setISBN(bookDTO.getIsbn());
        book.setBookName(bookDTO.getBookname());
        book.setPublishYear(bookDTO.getPublish_year());
        book.setAddedBy(userOpt.get());

        BookDomain savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @Operation(summary = "Actualizar libro", description = "Actualiza un libro existente según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDomain.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookDomain> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDomain bookDetails) {

        Optional<BookDomain> updatedBook = bookService.updateBook(id, bookDetails);
        return updatedBook.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar libro", description = "Elimina un libro según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}