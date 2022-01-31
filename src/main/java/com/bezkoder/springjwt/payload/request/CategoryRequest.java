package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class CategoryRequest {
  private Long id;
  private String name;
}
