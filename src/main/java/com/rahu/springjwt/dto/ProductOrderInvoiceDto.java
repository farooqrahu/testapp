package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductSale;
import com.rahu.springjwt.util.Utility;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderInvoiceDto {

  private Long id;
  private Long invoiceNo;
  private Float grandTotal;
  private Long totalQuantity;
  private String createdAt;
  private List<ProductSaleDto> productSales;

  public static ProductOrderInvoiceDto factoryProductOrderInvoice(ProductOrder productOrder) {
    long count = productOrder.getProductSales().stream().mapToLong(ProductSale::getQuantity).sum();
    List<ProductSaleDto> productSales = productOrder.getProductSales().stream().map(ProductSaleDto::factoryProductSale).filter(productSaleDto -> !productSaleDto.isReturned()).collect(Collectors.toList());
    if (!productSales.isEmpty()) {
      return ProductOrderInvoiceDto.builder().id(productOrder.getId()).createdAt(Utility.formatDate(productOrder.getCreatedAt(), "dd-MM-yyyy")).totalQuantity(count).grandTotal(productOrder.getGrandTotal()).invoiceNo(productOrder.getInvoiceNo()).productSales(productSales).build();
    } else {
      return null;
    }
  }
}
