package com.rahu.springjwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardDto {
  private Long id;
  private Long todaySaleCount;
  private Long totalCustomers;
  private Double todayAmount;
  private Long todayReturnCount;
  private Double todayReturnAmount;
  private Long totalSaleCount;
  private Double totalAmount;
}
