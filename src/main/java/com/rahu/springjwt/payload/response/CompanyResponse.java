package com.rahu.springjwt.payload.response;

import com.rahu.springjwt.models.Company;
import lombok.Data;

import java.util.List;

@Data
public class CompanyResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String name;

  private List<Company> companies;

  public CompanyResponse(List<Company> companies) {
    this.companies = companies;
  }

}
