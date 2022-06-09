package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.ProductSale;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSaleDto {

  private Long id;
  private ProductDto product;
  private Long quantity;
  private Long extra;
  private Long bundle;
  private String detail;

  public static  ProductSaleDto factoryProductSale(ProductSale productSale) {
return ProductSaleDto.builder().id(productSale.getId()).extra(productSale.getExtra()).bundle(productSale.getBundle()).quantity(productSale.getQuantity()).product(ProductDto.factoryProduct(productSale.getProduct())).build();
  }


  }
