package com.movies.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
public class Actor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "birth_year")
  private Integer birthYear;

  @Column(name = "country")
  private String country;

  @Column(name = "photo_url", length = 1000)
  private String photoUrl;

  @ManyToMany
  @JoinTable(
      name = "actor_films",
      joinColumns = @JoinColumn(name = "actor_id"),
      inverseJoinColumns = @JoinColumn(name = "film_id")
  )
  private Set<Film> films = new LinkedHashSet<>();

  public Actor() {
  }

  // ===== getters/setters =====

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Integer getBirthYear() {
    return birthYear;
  }

  public void setBirthYear(Integer birthYear) {
    this.birthYear = birthYear;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public Set<Film> getFilms() {
    return films;
  }

  public void setFilms(Set<Film> films) {
    this.films = films;
  }
}
