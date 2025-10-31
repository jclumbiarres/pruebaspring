package org.lumbi.ejercicio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "books")
public class BookDomain extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotBlank
    @Column(nullable = false)
    private String bookname;

    @NotNull
    @Column(nullable = false)
    private Integer publish_year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserDomain addedBy;

    public BookDomain() {
    }

    public Long getId() {
        return id;
    }

    public String getBookName() {
        return bookname;
    }

    public String getISBN() {
        return isbn;
    }

    public Integer getPublishYear() {
        return publish_year;
    }

    public UserDomain getAddedBy() {
        return addedBy;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public void setBookName(String name) {
        this.bookname = name;
    }

    public void setPublishYear(Integer year) {
        this.publish_year = year;
    }

    public void setAddedBy(UserDomain addedBy) {
        this.addedBy = addedBy;
    }
}
