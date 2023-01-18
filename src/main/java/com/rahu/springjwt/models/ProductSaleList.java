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
@Table(name = "productsalelist")
public class ProductSaleList {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Nullable
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  private Product product;
  private Long totalQuantitySale;
  private Long extraSale;
  private Long bundleSale;
  private String detail;
  @Column(columnDefinition = "boolean default false")
  private boolean isReturned = false;
  @Nullable
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  private ProductOrder productOrder;


}
