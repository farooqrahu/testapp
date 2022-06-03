package com.rahu.springjwt.payload.request;

import lombok.Data;

@Data
public class CompanyRequest {
  private String username;
  private String password;
  private Long id;
  private String name;
}
