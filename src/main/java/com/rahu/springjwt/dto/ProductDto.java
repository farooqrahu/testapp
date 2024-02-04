package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Product;
import com.rahu.springjwt.models.ProductOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
public class ProductDto {
  private Long id;
  private String name;
  private String description = "";
  private Float price;
  private Float wholeSalePrice;
  private Long quantityItem;
  private Long quantityBundle;
  private Long extraQuantity;
  private Long quantity;
  private boolean enableTQ;
  private CategoryDto category;
  private CompanyDto company;
  private String file;
  private boolean images;
  private Date createdAt;
  private boolean outOfStock;
  private boolean wareHouseProduct;
  private List<ProductDto> prodContent;
  private int currentpage;
  private long totalitems;
  private int totalpages;

  public static ProductDto factoryProduct(Product product) {
    return ProductDto.builder().id(product.getId()).name(product.getName()).description(product.getDescription()).price(product.getPrice()).wholeSalePrice(product.getWholeSalePrice()).outOfStock(product.getOutOfStock())
      .createdAt(product.getCreatedAt()).quantityItem(product.getQuantityItem()).wareHouseProduct(product.getWareHouseProduct()).quantityBundle(product.getQuantityBundle()).extraQuantity(product.getExtraQuantity()).quantity(product.getQuantity()).enableTQ(product.isEnableTQ()).category(CategoryDto.factoryCategoryDto(product.getCategory())).company(CompanyDto.factoryCompanyDto(product.getCompany())).file(product.getFiles() != null ? "data:image/jpg;base64,"+ Base64.getEncoder().encodeToString(product.getFiles().getData()) : "").build();
  }
}
