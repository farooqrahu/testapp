package com.bezkoder.springjwt.dto;

import com.bezkoder.springjwt.models.ProductOrder;
import com.bezkoder.springjwt.models.ProductSale;
import com.bezkoder.springjwt.util.Utility;
import lombok.*;

import javax.swing.text.Utilities;
import java.util.Date;
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
    long count=productOrder.getProductSales().stream().mapToLong(ProductSale::getQuantity).sum();
    return ProductOrderInvoiceDto.builder().id(productOrder.getId()).createdAt(Utility.formatDate(productOrder.getCreatedAt(),"dd-MM-yyyy")).totalQuantity(count).grandTotal(productOrder.getGrandTotal()).invoiceNo(productOrder.getInvoiceNo()).productSales(productOrder.getProductSales().stream().map(ProductSaleDto::factoryProductSale).collect(Collectors.toList())).build();
  }
}
