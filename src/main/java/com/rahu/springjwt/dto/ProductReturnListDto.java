package com.rahu.springjwt.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rahu.springjwt.models.ProductReturn;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReturnListDto {

  private Long id;
  private Long totalQuantityReturn;
  private Long product;
  private Long extraReturn;
  private Long bundleReturn;
  private String detail;
  private ProductReturn productReturn;
}
