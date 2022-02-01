package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.dto.UserDto;
import com.bezkoder.springjwt.models.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserResponse {
  private String token;
  private String type = "Bearer";
  private UserDto user;

  public static UserResponse userResponseFactory(User user, String token) {
    return UserResponse.builder().token(token).user(UserDto.userDtoFactory(user)).build();
  }
}
