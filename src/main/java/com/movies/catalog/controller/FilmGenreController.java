package com.movies.catalog.controller;

import com.movies.catalog.entity.Film;
import com.movies.catalog.entity.FilmGenre;
import com.movies.catalog.entity.FilmGenreId;
import com.movies.catalog.entity.Genre;
import com.movies.catalog.repository.FilmGenreRepository;
import com.movies.catalog.repository.FilmRepository;
import com.movies.catalog.repository.GenreRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/film-genres")
@RequiredArgsConstructor
public class FilmGenreController {

  private final FilmGenreRepository filmGenreRepository;
  private final FilmRepository filmRepository;
  private final GenreRepository genreRepository;

  @GetMapping
  public List<FilmGenreDto> list() {
    return filmGenreRepository.findAllWithRefs().stream()
        .map(fg -> new FilmGenreDto(
            fg.getFilm().getId(),
            fg.getFilm().getTitle(),
            fg.getGenre().getId(),
            fg.getGenre().getName()
        ))
        .toList();
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody LinkRequest req) {
    Film film = filmRepository.findById(req.getFilmId()).orElse(null);
      if (film == null) {
          return ResponseEntity.badRequest().body("Фильм не найден: " + req.getFilmId());
      }

    Genre genre = genreRepository.findById(req.getGenreId()).orElse(null);
      if (genre == null) {
          return ResponseEntity.badRequest().body("Жанр не найден: " + req.getGenreId());
      }

    FilmGenreId id = new FilmGenreId(film.getId(), genre.getId());
    if (filmGenreRepository.existsById(id)) {
      return ResponseEntity.badRequest().body("Связь уже существует");
    }

    FilmGenre fg = new FilmGenre(id, film, genre);
    filmGenreRepository.save(fg);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{filmId}/{genreId}")
  public ResponseEntity<?> delete(@PathVariable Long filmId, @PathVariable Long genreId) {
    FilmGenreId id = new FilmGenreId(filmId, genreId);
    if (!filmGenreRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    filmGenreRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  // "Редактирование" связи: удаляем старую и создаём новую
  @PutMapping("/{oldFilmId}/{oldGenreId}")
  public ResponseEntity<?> update(@PathVariable Long oldFilmId,
      @PathVariable Long oldGenreId,
      @Valid @RequestBody LinkRequest req) {

    FilmGenreId oldId = new FilmGenreId(oldFilmId, oldGenreId);
    if (!filmGenreRepository.existsById(oldId)) {
      return ResponseEntity.notFound().build();
    }

    Film film = filmRepository.findById(req.getFilmId()).orElse(null);
      if (film == null) {
          return ResponseEntity.badRequest().body("Фильм не найден: " + req.getFilmId());
      }

    Genre genre = genreRepository.findById(req.getGenreId()).orElse(null);
      if (genre == null) {
          return ResponseEntity.badRequest().body("Жанр не найден: " + req.getGenreId());
      }

    FilmGenreId newId = new FilmGenreId(film.getId(), genre.getId());
    if (!oldId.equals(newId) && filmGenreRepository.existsById(newId)) {
      return ResponseEntity.badRequest().body("Такая связь уже существует");
    }

    filmGenreRepository.deleteById(oldId);
    filmGenreRepository.save(new FilmGenre(newId, film, genre));

    return ResponseEntity.ok().build();
  }

  @Data
  public static class LinkRequest {

    @NotNull
    private Long filmId;
    @NotNull
    private Long genreId;
  }

  @Data
  @AllArgsConstructor
  public static class FilmGenreDto {

    private Long filmId;
    private String filmTitle;
    private Long genreId;
    private String genreName;
  }
}
