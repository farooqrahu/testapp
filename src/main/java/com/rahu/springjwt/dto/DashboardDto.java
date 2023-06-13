package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Category;
import com.rahu.springjwt.models.ProductOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardDto {
  private Long id;
  private Integer totalSales;
  private Double totalAmount;

  private Integer grandSales;
  private Double grandAmount;
}
