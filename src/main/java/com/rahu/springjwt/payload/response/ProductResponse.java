package com.rahu.springjwt.payload.response;

import java.util.List;

import com.rahu.springjwt.dto.ProductDto;
import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.models.Category;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class ProductResponse {
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
  private int currentpage;
  private long totalitems;
  private int totalpages;

  public ProductResponse(String token, Long id, Category category, String name, String description, Float price,
      boolean images) {
    this.token = token;
    this.id = id;
    this.category = category;
    this.name = name;
    this.description = description;
    this.price = price;
    this.images = images;
  }

  public ProductResponse(String token, List<ProductDto> list) {
    this.token = token;
    this.list = list;
  }

  public ProductResponse(List<ProductOrderInvoiceDto> productOrderInvoiceDtos,String t) {
    this.productOrderInvoiceDtos = productOrderInvoiceDtos;
  }
  public ProductResponse(List<ProductDto> list) {
    this.list = list;
  }

  public ProductResponse(Page<ProductDto> list) {
    this.list = list.getContent();
    this.currentpage = list.getNumber();
    this.totalitems = list.getTotalElements();
    this.totalpages = list.getTotalPages();
  }

}
