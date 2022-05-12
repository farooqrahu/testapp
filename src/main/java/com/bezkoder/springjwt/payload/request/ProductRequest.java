package com.bezkoder.springjwt.payload.request;

import com.bezkoder.springjwt.models.Category;
import com.bezkoder.springjwt.models.Company;
import lombok.Data;

@Data
public class ProductRequest {
  private Long id;
  private Category category;
  private Company company;
  private String name;
  private String description;
  private Float price;
  private boolean images;
  private String sort = "id";
  private String sortdirection = "asc";
  private int pagesize = 0;
  private int pagenumber = 0;
  private Long quantityItem;
  private Long quantityBundle;
  private Long extraQuantity;
  private Long quantity;
}
