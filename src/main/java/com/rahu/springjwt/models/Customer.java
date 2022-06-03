package com.rahu.springjwt.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@Setter
@Entity
@Table(name = "customers")
@Builder
@AllArgsConstructor
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(unique = true)
  @Size(max = 20)
  private String name;
  private String mobileNumber;
  private String address;

  public Customer(String name) {
    this.name = name;
  }

  public Customer(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Customer() {
  }

}
