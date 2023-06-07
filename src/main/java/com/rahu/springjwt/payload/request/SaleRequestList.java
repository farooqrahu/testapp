package com.rahu.springjwt.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class SaleRequestList implements Serializable {
 private List<SaleRequest> data;
  private Double grandTotal;
  private Long grandTotalQtReturn;
  private String customerName;
  private String mobileNumber;
  private String address;
 private Long id;
}
