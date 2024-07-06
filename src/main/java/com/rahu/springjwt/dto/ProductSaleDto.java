package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductSaleList;
import lombok.*;

import java.util.Date;
import java.util.Objects;

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
  private String priceSelected;
  private Date createdAt;
  private String customerName;
  private Long invoiceNo;
  private boolean isReturned = false;

  public static ProductSaleDto factoryProductSale(ProductSaleList productSaleList) {
    ProductOrder prodOrder=productSaleList.getProductOrder();
    String custName="";
    if(Objects.requireNonNull(prodOrder).getCustomer()!=null){
      custName=""+ (prodOrder.getCustomer() != null ? prodOrder.getCustomer().getName() : null);
    }
    return ProductSaleDto.builder().invoiceNo(prodOrder.getInvoiceNo()).customerName(custName).id(productSaleList.getId()).priceSelected(productSaleList.getPriceSelected()).extraSale(productSaleList.getExtraSale()).bundleSale(productSaleList.getBundleSale()).totalQuantitySale(productSaleList.getTotalQuantitySale()).createdAt(productSaleList.getCreatedAt()).product(ProductDto.factoryProduct(productSaleList.getProduct())).build();
  }


}
