package com.movies.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "films",
    uniqueConstraints = @UniqueConstraint(name = "films_title_year_unique", columnNames = {"title",
        "release_year"})
)
public class Film {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Название обязательно")
  @Column(nullable = false)
  private String title;

  @Min(value = 1888, message = "Год должен быть >= 1888")
  @Max(value = 2100, message = "Год должен быть <= 2100")
  @Column(name = "release_year")
  private Integer releaseYear;

  @Min(value = 1, message = "Длительность должна быть > 0")
  @Max(value = 1000, message = "Длительность слишком большая")
  @Column(name = "runtime_min")
  private Integer runtimeMin;

  @Column
  private String description;

  // Связь many-to-many через film_genres(film_id, genre_id)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "film_genres",
      joinColumns = @JoinColumn(name = "film_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id")
  )
  private Set<Genre> genres = new LinkedHashSet<>();
}
