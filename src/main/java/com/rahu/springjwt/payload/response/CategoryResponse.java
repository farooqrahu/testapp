package com.rahu.springjwt.payload.response;

import java.util.List;

import com.rahu.springjwt.models.Category;

import lombok.Data;

@Data
public class CategoryResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String name;

  private List<Category> Categories;

  public CategoryResponse(List<Category> Categories) {
    this.Categories = Categories;
  }

}
