package com.bezkoder.springjwt.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@Setter
@Entity
@Table(name = "Company")
@Builder
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(unique = true)
  @Size(max = 20)
  private String name;

  public Company(String name) {
    this.name = name;
  }

  public Company(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Company() {
  }

}
