package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;
import java.util.Date;

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
  private boolean enableTQ;
  private CategoryDto category;
  private CompanyDto company;
  private String file;
  private boolean images;
  private Date createdAt;
  private boolean outOfStock;

  public static ProductDto factoryProduct(Product product) {
    return ProductDto.builder().id(product.getId()).name(product.getName()).description(product.getDescription()).price(product.getPrice()).outOfStock(product.getOutOfStock())
      .createdAt(product.getCreatedAt()).quantityItem(product.getQuantityItem()).quantityBundle(product.getQuantityBundle()).extraQuantity(product.getExtraQuantity()).quantity(product.getQuantity()).enableTQ(product.isEnableTQ()).category(CategoryDto.factoryCategoryDto(product.getCategory())).company(CompanyDto.factoryCompanyDto(product.getCompany())).file(product.getFiles() != null ? "data:image/jpg;base64,"+ Base64.getEncoder().encodeToString(product.getFiles().getData()) : "").build();
  }
}
