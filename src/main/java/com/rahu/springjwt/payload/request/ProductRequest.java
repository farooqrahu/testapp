package com.rahu.springjwt.payload.request;

import com.rahu.springjwt.models.Category;
import com.rahu.springjwt.models.Company;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductRequest {
  private Long id;
  private Category category;
  private Company company;
  private String name;
  private Long invoiceNo;
  private String description;
  private Float price;
  private Float wholeSalePrice;
  private boolean images;
  private String sort = "createdAt";
  private String sortdirection = "desc";
  private int pagesize = 0;
  private int pagenumber = 0;
  private Long quantityItem;
  private Long quantityBundle;
  private Long extraQuantity;
  private Long quantity;
  private boolean enableTQ;
  private boolean wareHouseProduct;
  private boolean outOfStock;

}
