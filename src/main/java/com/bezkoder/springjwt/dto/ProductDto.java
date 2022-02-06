package com.bezkoder.springjwt.dto;

import com.bezkoder.springjwt.models.Category;
import com.bezkoder.springjwt.models.Company;
import com.bezkoder.springjwt.models.FileDB;
import com.bezkoder.springjwt.models.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Setter
@Getter
@Builder
public class ProductDto {
  private Long id;
  private String name;
  private String description = "";
  private Float price;
  private Long quantityItem;
  private Long quantityBundle;
  private Long extraQuantity;
  private Long quantity;
  private Category category;
  private Company company;
  private String file;
  private boolean images;

  public static ProductDto factoryProduct(Product product) {
    return ProductDto.builder().id(product.getId()).name(product.getName()).description(product.getDescription()).price(product.getPrice())
      .quantityItem(product.getQuantityItem()).quantityBundle(product.getQuantityBundle()).extraQuantity(product.getExtraQuantity()).quantity(product.getQuantity()).category(product.getCategory()).company(product.getCompany())
      .file(product.getFiles() != null ? "data:image/jpg;base64,"+ Base64.getEncoder().encodeToString(product.getFiles().getData()) : "").build();
  }
}
