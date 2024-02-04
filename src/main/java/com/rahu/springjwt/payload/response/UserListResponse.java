package com.rahu.springjwt.payload.response;

import java.util.List;
import java.util.stream.Collectors;

import com.rahu.springjwt.dto.UserDto;
import com.rahu.springjwt.models.User;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class UserListResponse {

  private String token;
  private String type = "Bearer";
  private List<UserDto> users;


  public static UserListResponse userResponseFactory(List<User> users) {
    return UserListResponse.builder().users(users.stream().map(UserDto::userDtoFactory).collect(Collectors.toList())).build();
  }

}
