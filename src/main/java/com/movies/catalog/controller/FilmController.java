package com.movies.catalog.controller;

import com.movies.catalog.entity.Film;
import com.movies.catalog.entity.Genre;
import com.movies.catalog.repository.FilmRepository;
import com.movies.catalog.repository.GenreRepository;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/films")
public class FilmController {

  private final FilmRepository filmRepo;
  private final GenreRepository genreRepo;

  public FilmController(FilmRepository filmRepo, GenreRepository genreRepo) {
    this.filmRepo = filmRepo;
    this.genreRepo = genreRepo;
  }

  // ===== CRUD Films =====

  @GetMapping
  public List<Film> getAll() {
    return filmRepo.findAll();
  }

  @GetMapping("/{id}")
  public Film getOne(@PathVariable Long id) {
    return filmRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Film create(@Valid @RequestBody Film film) {
    film.setId(null);
    // genres через это API не трогаем, управляем отдельными эндпоинтами ниже
    return filmRepo.save(film);
  }

  @PutMapping("/{id}")
  public Film update(@PathVariable Long id, @Valid @RequestBody Film incoming) {
    Film existing = filmRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found"));

    existing.setTitle(incoming.getTitle());
    existing.setReleaseYear(incoming.getReleaseYear());
    existing.setRuntimeMin(incoming.getRuntimeMin());
    existing.setDescription(incoming.getDescription());

    return filmRepo.save(existing);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    if (!filmRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
    }
    filmRepo.deleteById(id);
  }

  // ===== Genres for a Film =====

  @GetMapping("/{id}/genres")
  public Set<Genre> getFilmGenres(@PathVariable Long id) {
    Film film = getOne(id);
    return film.getGenres();
  }

  // body: [1,2,3]  (genreIds)
  @PutMapping("/{id}/genres")
  public Set<Genre> setFilmGenres(@PathVariable Long id, @RequestBody List<Long> genreIds) {
    Film film = getOne(id);

    List<Genre> genres = genreRepo.findAllById(genreIds);
    if (genres.size() != genreIds.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some genreIds not found");
    }

    film.setGenres(new LinkedHashSet<>(genres));
    filmRepo.save(film);
    return film.getGenres();
  }
}
