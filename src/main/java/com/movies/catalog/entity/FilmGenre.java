package com.movies.catalog.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "film_genres")
public class FilmGenre {

  @EmbeddedId
  private FilmGenreId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("filmId")
  @JoinColumn(name = "film_id", nullable = false)
  private Film film;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("genreId")
  @JoinColumn(name = "genre_id", nullable = false)
  private Genre genre;
}
