package com.rahu.springjwt.payload.response;

import com.rahu.springjwt.models.Role;
import lombok.Data;

import java.util.List;

@Data
public class RoleResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String name;

  private List<Role> roles;

  public RoleResponse(List<Role> roles) {
    this.roles = roles;
  }

}
