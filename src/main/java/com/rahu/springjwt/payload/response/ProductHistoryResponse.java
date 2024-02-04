package com.rahu.springjwt.payload.response;

import com.rahu.springjwt.dto.ProductHistoryDto;
import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.models.Category;
import com.rahu.springjwt.models.ProductHistory;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductHistoryResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private Category category;
  private String name;
  private String description;
  private Float price;
  private boolean images;
  private List<ProductHistoryDto> productHistoryContent;
  private List<ProductHistoryDto> prodHisContent;
  private List<ProductOrderInvoiceDto> productOrderInvoiceDtos;
  private int currentpage;
  private long totalitems;
  private int totalpages;

  public ProductHistoryResponse(String token, Long id, Category category, String name, String description, Float price,
                                boolean images) {
    this.token = token;
    this.id = id;
    this.category = category;
    this.name = name;
    this.description = description;
    this.price = price;
    this.images = images;
  }

  public ProductHistoryResponse(String token, List<ProductHistoryDto> productHistoryContent) {
    this.token = token;
    this.productHistoryContent = productHistoryContent;
  }

  public ProductHistoryResponse(List<ProductOrderInvoiceDto> productOrderInvoiceDtos, String t) {
    this.productOrderInvoiceDtos = productOrderInvoiceDtos;
  }
//  public ProductHistoryResponse(List<ProductHistoryDto> productHistoryContent) {
//    this.productHistoryContent = productHistoryContent;
//    this.totalitems= productHistoryContent.size();
//  }

  public ProductHistoryResponse(Page<ProductHistory> productHistoryContent) {
    this.prodHisContent = productHistoryContent.getContent().stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList());
    this.currentpage = productHistoryContent.getNumber();
    this.totalitems = productHistoryContent.getTotalElements();
    this.totalpages = productHistoryContent.getTotalPages();
  }

}
