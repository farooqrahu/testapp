package com.bezkoder.springjwt.dto;

import com.bezkoder.springjwt.models.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
