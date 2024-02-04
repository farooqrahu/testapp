package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryDto {
  private Long id;
  private String name;


  public static CategoryDto factoryCategoryDto(Category category){
    return CategoryDto.builder().id(category.getId()).name(category.getName()).build();
  }

}
