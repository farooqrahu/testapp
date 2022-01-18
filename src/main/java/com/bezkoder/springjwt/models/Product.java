package com.bezkoder.springjwt.models;

import com.bezkoder.springjwt.payload.request.ProductRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@Entity
@Table(name = "Products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank
  @Size(max = 20)
  private String name;
  @Nullable
  private String description = "";
  private Float price;
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
  private Category category;

  @Nullable
  private boolean images;

  public Product(Long id, String name, String description, Float price, Category category, boolean images) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
    this.images = images;
  }

  public Product(ProductRequest productRequest) {
    this.id = productRequest.getId();
    this.name = productRequest.getName();
    this.description = productRequest.getDescription();
    this.price = productRequest.getPrice();
    this.category = productRequest.getCategory();
    this.images = productRequest.isImages();
  }

  public boolean isImages() {
    return this.images;
  }

  public Product(String name, String description, boolean images, Float price) {
    this.name = name;
    this.description = description;
    this.images = images;
    this.price = price;

  }

  public Product(String name, String description, Float price) {
    this.name = name;
    this.description = description;
    this.price = price;

  }

  public Product() {
  }

  public Product(String name, String description, Category category, boolean images, Float price) {
    this.name = name;
    this.description = description;
    this.category = category;
    this.images = images;
    this.price = price;

  }

  public Product(String name, String description, Category category, Float price) {
    this.name = name;
    this.description = description;
    this.category = category;
    this.price = price;

  }

}
