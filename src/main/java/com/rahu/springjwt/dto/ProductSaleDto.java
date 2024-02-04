package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.ProductSaleList;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSaleDto {

  private Long id;
  private ProductDto product;
  private Long totalQuantityReturn;
  private Long extraReturn;
  private Long bundleReturn;
  private Long totalQuantitySale;
  private Long extraSale;
  private Long bundleSale;
  private String detail;
  private Date createdAt;
  private boolean isReturned = false;

  public static ProductSaleDto factoryProductSale(ProductSaleList productSaleList) {
    return ProductSaleDto.builder().id(productSaleList.getId()).extraSale(productSaleList.getExtraSale()).bundleSale(productSaleList.getBundleSale()).totalQuantitySale(productSaleList.getTotalQuantitySale()).createdAt(productSaleList.getCreatedAt()).product(ProductDto.factoryProduct(productSaleList.getProduct())).build();
  }


}
