package com.rahu.springjwt.models;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "productsales")
public class ProductSale {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Nullable
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  private Product product;
  private Long quantity;
  private Long extra;
  private Long bundle;
  private String detail;
  @Nullable
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  private ProductOrder productOrder;


}
