package com.movies.catalog.repository;

import com.movies.catalog.entity.Actor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {

  @EntityGraph(attributePaths = {"films"})
  List<Actor> findAll();

  @EntityGraph(attributePaths = {"films"})
  Optional<Actor> findById(Long id);
}
