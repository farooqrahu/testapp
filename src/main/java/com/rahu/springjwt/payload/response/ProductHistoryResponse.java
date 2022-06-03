package com.rahu.springjwt.payload.response;

import com.rahu.springjwt.dto.ProductHistoryDto;
import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.models.Category;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

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
  private List<ProductHistoryDto> list;
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

  public ProductHistoryResponse(String token, List<ProductHistoryDto> list) {
    this.token = token;
    this.list = list;
  }

  public ProductHistoryResponse(List<ProductOrderInvoiceDto> productOrderInvoiceDtos, String t) {
    this.productOrderInvoiceDtos = productOrderInvoiceDtos;
  }
  public ProductHistoryResponse(List<ProductHistoryDto> list) {
    this.list = list;
  }

  public ProductHistoryResponse(Page<ProductHistoryDto> list) {
    this.list = list.getContent();
    this.currentpage = list.getNumber();
    this.totalitems = list.getTotalElements();
    this.totalpages = list.getTotalPages();
  }

}
