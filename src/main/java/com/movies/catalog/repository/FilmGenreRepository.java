package com.movies.catalog.repository;

import com.movies.catalog.entity.FilmGenre;
import com.movies.catalog.entity.FilmGenreId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FilmGenreRepository extends JpaRepository<FilmGenre, FilmGenreId> {

  @Query("""
      select fg from FilmGenre fg
      join fetch fg.film
      join fetch fg.genre
      """)
  List<FilmGenre> findAllWithRefs();
}
