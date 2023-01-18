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
@Table(name = "productreturnlist")
public class ProductReturnList {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Nullable
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  private Product product;
  private Long totalQuantityReturn;
  private Long extraReturn;
  private Long bundleReturn;
  private String detail;
  @Nullable
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  private ProductReturn productReturn;


}
