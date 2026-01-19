package com.movies.catalog.controller;

import com.movies.catalog.entity.Genre;
import com.movies.catalog.repository.GenreRepository;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/api/genres")
public class GenreController {

  private final GenreRepository repo;

  public GenreController(GenreRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Genre> getAll() {
    return repo.findAll(org.springframework.data.domain.Sort.by("id"));
  }


  @GetMapping("/{id}")
  public Genre getById(@PathVariable Long id) {
    return repo.findById(id).orElseThrow(() -> new RuntimeException("Genre not found: id=" + id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Genre create(@Valid @RequestBody Genre genre) {
    genre.setId(null);
    return repo.save(genre);
  }

  @PutMapping("/{id}")
  public Genre update(@PathVariable Long id, @Valid @RequestBody Genre genre) {
    Genre existing = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Genre not found: id=" + id));
    existing.setName(genre.getName());
    return repo.save(existing);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    repo.deleteById(id);
  }
}
