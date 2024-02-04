package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.ProductHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Builder
public class ProductHistoryDto {
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
  private String userName;
  private Date createdAt;

  public static ProductHistoryDto factoryProduct(ProductHistory product) {
    return ProductHistoryDto.builder().id(product.getId()).name(product.getName()).description(product.getDescription()).price(product.getPrice())
      .quantityItem(product.getQuantityItem()).quantityBundle(product.getQuantityBundle()).extraQuantity(product.getExtraQuantity()).quantity(product.getQuantity()).category(CategoryDto.factoryCategoryDto(product.getCategory())).company(CompanyDto.factoryCompanyDto(product.getCompany())).userName(product.getUpdatedByUser().getName()).createdAt(product.getCreatedAt()).build();
  }
}
