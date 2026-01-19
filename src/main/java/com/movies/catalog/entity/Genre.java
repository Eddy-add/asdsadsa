package com.movies.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "genres",
        uniqueConstraints = @UniqueConstraint(name = "genres_name_unique", columnNames = "name")
)
@JsonIgnoreProperties({"films"}) // чтобы не было бесконечной рекурсии в JSON
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название жанра обязательно")
    @Column(nullable = false)
    private String name;
}
