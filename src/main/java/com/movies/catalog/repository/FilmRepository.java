package com.movies.catalog.repository;

import com.movies.catalog.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Long> {

}
