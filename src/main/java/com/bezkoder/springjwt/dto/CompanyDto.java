package com.bezkoder.springjwt.dto;

import com.bezkoder.springjwt.models.Company;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompanyDto {
  private Long id;
  private String name;
  public static CompanyDto factoryCompanyDto(Company company){
    return CompanyDto.builder().id(company.getId()).name(company.getName()).build();
  }

}
