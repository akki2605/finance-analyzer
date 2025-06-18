package com.codeyantratech.financeanalyzer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a transaction category.
 * Each category belongs to a specific user.
 */
@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Size(max = 7, message = "Color must be a valid hex code")
    @Column(length = 7)
    private String color; // Hex color code like #FF5733

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Category(String name, String description, String color, User user) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.user = user;
    }
}