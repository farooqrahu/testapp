package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CustomerDto {
  private Long id;
  private String name;
  private String customerId;
  private String mobileNumber;
  private String address;

  public CustomerDto(String name) {
    this.name = name;
  }

  public CustomerDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public static CustomerDto factoryCustomerDto(Customer customer) {
    return CustomerDto.builder().id(customer.getId()).name(customer.getName()).mobileNumber(customer.getMobileNumber()).address(customer.getAddress()).build();
  }
}
