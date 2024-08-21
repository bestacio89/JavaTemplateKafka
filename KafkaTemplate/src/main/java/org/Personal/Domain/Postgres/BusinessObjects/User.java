package org.Personal.Domain.Postgres.BusinessObjects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.Personal.Domain.Generic.IEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User implements IEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email should not exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    @Override
    public LocalDateTime getCreatedDate() {
        return createdAt;
    }

    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdAt = createdDate;
    }

    @Override
    public LocalDateTime getUpdatedDate() {
        return this.updatedAt;
    }

    @Override
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedAt = updatedDate;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
