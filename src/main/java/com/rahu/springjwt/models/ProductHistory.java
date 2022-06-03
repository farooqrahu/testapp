package com.rahu.springjwt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@Entity
@Table(name = "ProductHistory")
@Builder
@AllArgsConstructor
public class ProductHistory extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank
  @Size(max = 20)
  private String name;
  @Nullable
  private String description = "";
  private Float price;
  @OneToOne(fetch = FetchType.LAZY)
  private User updatedByUser;
  private Long quantityItem;
  private Long quantityBundle;
  private Long extraQuantity;
  private Long quantity;
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  private Category category;
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  private Company company;
  @Nullable
  @OneToOne(fetch = FetchType.LAZY)
  private Product product;

  public ProductHistory() {
  }
}
