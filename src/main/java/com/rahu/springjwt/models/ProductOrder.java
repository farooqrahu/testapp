package com.rahu.springjwt.models;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "productorders")
public class ProductOrder extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long invoiceNo;
  private Float grandTotal;
  @Nullable
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
  private Customer customer;

  @OneToMany(mappedBy = "productOrder")
  private List<ProductSale> productSales;
  @Column(columnDefinition = "boolean default false")
  private boolean isReturned = false;
}
