package com.rahu.springjwt.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "generalledger")
public class GeneralLedger extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String description;
  private LocalDate date;
  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;
  private Double debit;
  private Double credit;
  @ManyToOne
  private ProductOrder productOrder;

  @ManyToOne
  private Customer customer;

}
