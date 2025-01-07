package com.rahu.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "account")
@Data
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String type;
  private BigDecimal balance = BigDecimal.ZERO;

  // Getters and Setters

  public void addBalance(BigDecimal amount) {
      this.balance = this.balance.add(amount);
  }
  public void substractBalance(BigDecimal amount) {
      this.balance = this.balance.subtract(amount);
  }
}
