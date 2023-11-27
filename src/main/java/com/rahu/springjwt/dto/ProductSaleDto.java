package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.ProductSaleList;
import lombok.*;

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
  private boolean isReturned = false;

  public static ProductSaleDto factoryProductSale(ProductSaleList productSaleList) {
    return ProductSaleDto.builder().id(productSaleList.getId()).extraSale(productSaleList.getExtraSale()).bundleSale(productSaleList.getBundleSale()).totalQuantitySale(productSaleList.getTotalQuantitySale()).product(ProductDto.factoryProduct(productSaleList.getProduct())).build();
  }


}
