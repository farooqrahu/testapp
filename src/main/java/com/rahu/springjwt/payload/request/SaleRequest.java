package com.rahu.springjwt.payload.request;

import com.rahu.springjwt.dto.ProductDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SaleRequest {
  private Long id;
  private Long productId;
  private ProductDto product;
  private String name;
  private Long quantity;
  public long userQuantityBundle;
  public long userExtraQuantity;
  public long userTotalQuantity;
}
