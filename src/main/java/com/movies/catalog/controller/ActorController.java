package com.movies.catalog.controller;

import com.movies.catalog.entity.Actor;
import com.movies.catalog.entity.Film;
import com.movies.catalog.repository.ActorRepository;
import com.movies.catalog.repository.FilmRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

  private final ActorRepository actorRepo;
  private final FilmRepository filmRepo;

  public ActorController(ActorRepository actorRepo, FilmRepository filmRepo) {
    this.actorRepo = actorRepo;
    this.filmRepo = filmRepo;
  }

  @GetMapping
  public List<ActorDto> getAll() {
    return actorRepo.findAll().stream()
        .sorted(Comparator.comparing(a -> safe(a.getFullName())))
        .map(ActorController::toDto)
        .toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ActorDto> getOne(@PathVariable Long id) {
    return actorRepo.findById(id)
        .map(ActorController::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<ActorDto> create(@Valid @RequestBody ActorUpsertRequest req) {
    Actor a = new Actor();
    apply(a, req);
    Actor saved = actorRepo.save(a);
    return ResponseEntity.ok(toDto(saved));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ActorDto> update(@PathVariable Long id,
      @Valid @RequestBody ActorUpsertRequest req) {
    Actor a = actorRepo.findById(id).orElse(null);
      if (a == null) {
          return ResponseEntity.notFound().build();
      }

    apply(a, req);
    Actor saved = actorRepo.save(a);
    return ResponseEntity.ok(toDto(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Actor a = actorRepo.findById(id).orElse(null);
      if (a == null) {
          return ResponseEntity.notFound().build();
      }

    // на всякий случай чистим связи, чтобы не упереться в FK в join-таблице
    a.getFilms().clear();
    actorRepo.save(a);

    actorRepo.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // ===== helpers =====

  private void apply(Actor a, ActorUpsertRequest req) {
    a.setFullName(req.fullName().trim());
    a.setBirthYear(req.birthYear());
    a.setCountry(blankToNull(req.country()));
    a.setPhotoUrl(blankToNull(req.photoUrl()));

    Set<Film> films = new LinkedHashSet<>();
    if (req.filmIds() != null && !req.filmIds().isEmpty()) {
      List<Film> found = filmRepo.findAllById(req.filmIds());
      films.addAll(found);
    }
    a.setFilms(films);
  }

  private static ActorDto toDto(Actor a) {
    List<FilmShort> films = a.getFilms().stream()
        .sorted(Comparator.comparing(f -> safe(f.getTitle())))
        .map(f -> new FilmShort(f.getId(), f.getTitle(), f.getReleaseYear()))
        .toList();

    List<Long> filmIds = films.stream().map(FilmShort::id).toList();

    return new ActorDto(
        a.getId(),
        a.getFullName(),
        a.getBirthYear(),
        a.getCountry(),
        a.getPhotoUrl(),
        filmIds,
        films
    );
  }

  private static String blankToNull(String s) {
      if (s == null) {
          return null;
      }
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private static String safe(String s) {
    return s == null ? "" : s;
  }

  // ===== DTOs =====

  public record ActorUpsertRequest(
      @NotBlank String fullName,
      Integer birthYear,
      String country,
      String photoUrl,
      List<Long> filmIds
  ) {

  }

  public record ActorDto(
      Long id,
      String fullName,
      Integer birthYear,
      String country,
      String photoUrl,
      List<Long> filmIds,
      List<FilmShort> films
  ) {

  }

  public record FilmShort(
      Long id,
      String title,
      Integer releaseYear
  ) {

  }
}
