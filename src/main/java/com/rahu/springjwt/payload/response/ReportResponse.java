package com.rahu.springjwt.payload.response;

import com.rahu.springjwt.dto.ProductDto;
import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.dto.ProductSaleDto;
import com.rahu.springjwt.models.Category;
import com.rahu.springjwt.models.Product;
import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductSaleList;
import com.rahu.springjwt.util.Utility;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class ReportResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private Category category;
  private String name;
  private String description;
  private Float price;
  private boolean images;
  private List<ProductDto> list;
  private List<ProductOrderInvoiceDto> productOrderInvoiceDtos;
  private List<ProductSaleDto> prodContent;
  private int currentpage;
  private long totalitems;
  private int totalpages;

  public ReportResponse(String token, Long id, Category category, String name, String description, Float price,
                        boolean images) {
    this.token = token;
    this.id = id;
    this.category = category;
    this.name = name;
    this.description = description;
    this.price = price;
    this.images = images;
  }

  public ReportResponse(String token, List<ProductDto> list) {
    this.token = token;
    this.list = list;
  }

  public ReportResponse(List<ProductOrderInvoiceDto> productOrderInvoiceDtos, String t) {
    this.productOrderInvoiceDtos = productOrderInvoiceDtos;
  }
  public ReportResponse(List<ProductDto> list) {
    this.list = list;
  }

  public ReportResponse(Page<ProductSaleList> productPage) {
    this.prodContent = productPage.getContent().stream().map(ProductSaleDto::factoryProductSale).filter(Objects::nonNull).collect(Collectors.toList());
    this.totalitems = productPage.getTotalElements();
    this.totalpages = productPage.getTotalPages();
  }

}
