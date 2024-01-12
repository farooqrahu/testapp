package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Product;
import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductSaleList;
import com.rahu.springjwt.util.Utility;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderInvoiceDto {

  private Long id;
  private Long invoiceNo;
  private Double grandTotal;
  private Long totalQuantity;
  private String createdAt;
  String customerName = null;
  String customerId = null;
  String mobileNumber = null;
  private boolean isReturned = false;
  private List<ProductSaleDto> productSales;

  private List<ProductOrderInvoiceDto> saleOrders;
  private int currentpage;
  private long totalitems;
  private int totalpages;

  public ProductOrderInvoiceDto(Page<ProductOrder> productOrders) {
    this.saleOrders = productOrders.getContent().stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).filter(Objects::nonNull).collect(Collectors.toList());
    this.currentpage = productOrders.getNumber();
    this.totalitems = productOrders.getTotalElements();
    this.totalpages = productOrders.getTotalPages();
  }

  public static ProductOrderInvoiceDto factoryProductOrderInvoice(ProductOrder productOrder) {
    String customerName = "";
    String mobileNumber = "";
    String customerId = "";
    if (productOrder.getCustomer() != null ) {
      customerName = productOrder.getCustomer().getName();
      mobileNumber = productOrder.getCustomer().getMobileNumber();
      customerId = productOrder.getCustomer().getCustomerCode();
    }
    long count = productOrder.getProductSaleLists().stream().mapToLong(ProductSaleList::getTotalQuantitySale).sum();
//    List<ProductSaleDto> productSales = productOrder.getProductSales().stream().map(ProductSaleDto::factoryProductSale).filter(productSaleDto -> !productSaleDto.isReturned()).collect(Collectors.toList());
    List<ProductSaleDto> productSales = productOrder.getProductSaleLists().stream().map(ProductSaleDto::factoryProductSale).collect(Collectors.toList());
    if (!productSales.isEmpty()) {
//      List<ProductSaleDto> isReturned=productSales.stream().filter(ProductSaleDto::isReturned).collect(Collectors.toList());
      return ProductOrderInvoiceDto.builder().id(productOrder.getId()).customerId(customerId).customerName(customerName).mobileNumber(mobileNumber).isReturned(productOrder.isReturned()).createdAt(Utility.formatDate(productOrder.getCreatedAt(), "dd-MM-yyyy hh:mm:ss")).totalQuantity(count).grandTotal(productOrder.getGrandTotal()).invoiceNo(productOrder.getInvoiceNo()).productSales(productSales).build();
    } else {
      return null;
    }
  }
}
