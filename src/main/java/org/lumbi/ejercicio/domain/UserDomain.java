package org.lumbi.ejercicio.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class UserDomain extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BookDomain> addedBooks;

    public UserDomain() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BookDomain> getAddedBooks() {
        return addedBooks;
    }

    public void setAddedBooks(List<BookDomain> addedBooks) {
        this.addedBooks = addedBooks;
    }

    public void addBook(BookDomain book) {
        addedBooks.add(book);
        book.setAddedBy(this);
    }

    public void removeBook(BookDomain book) {
        addedBooks.remove(book);
        book.setAddedBy(null);
    }
}