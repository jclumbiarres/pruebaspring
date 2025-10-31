package org.lumbi.ejercicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookRequestDTO {

    @NotBlank(message = "ISBN no debe estar vacío")
    private String isbn;

    @NotBlank(message = "Nombre del libro no debe estar vacío")
    private String bookname;

    @NotNull(message = "Año de publicación no debe ser nulo")
    private Integer publish_year;

    @NotNull(message = "ID del usuario que agrega el libro es obligatorio")
    private Long addedById;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public Integer getPublish_year() {
        return publish_year;
    }

    public void setPublish_year(Integer publish_year) {
        this.publish_year = publish_year;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }
}
