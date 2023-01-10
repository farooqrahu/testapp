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
  String customerName = null;
  String mobileNumber = null;
  private boolean isReturned = false;
  private List<ProductSaleDto> productSales;

  public static ProductOrderInvoiceDto factoryProductOrderInvoice(ProductOrder productOrder) {
    String customerName = "";
    String mobileNumber = "";
    if (productOrder.getCustomer() != null ) {
      customerName = productOrder.getCustomer().getName();
      mobileNumber = productOrder.getCustomer().getMobileNumber();
    }
    long count = productOrder.getProductSales().stream().mapToLong(ProductSale::getQuantity).sum();
//    List<ProductSaleDto> productSales = productOrder.getProductSales().stream().map(ProductSaleDto::factoryProductSale).filter(productSaleDto -> !productSaleDto.isReturned()).collect(Collectors.toList());
    List<ProductSaleDto> productSales = productOrder.getProductSales().stream().map(ProductSaleDto::factoryProductSale).collect(Collectors.toList());
    if (!productSales.isEmpty()) {
//      List<ProductSaleDto> isReturned=productSales.stream().filter(ProductSaleDto::isReturned).collect(Collectors.toList());
      return ProductOrderInvoiceDto.builder().id(productOrder.getId()).customerName(customerName).mobileNumber(mobileNumber).isReturned(productOrder.isReturned()).createdAt(Utility.formatDate(productOrder.getCreatedAt(), "dd-MM-yyyy hh:mm:ss")).totalQuantity(count).grandTotal(productOrder.getGrandTotal()).invoiceNo(productOrder.getInvoiceNo()).productSales(productSales).build();
    } else {
      return null;
    }
  }
}
