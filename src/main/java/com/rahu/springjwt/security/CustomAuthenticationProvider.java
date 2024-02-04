package com.rahu.springjwt.security;


import com.rahu.springjwt.models.User;
import com.rahu.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserRepository userRepository;

  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isPresent() && !username.contains(user.get().getUsername())) {
      throw new BadCredentialsException("");
    } else if (user.isPresent() && !encoder.matches(password, user.get().getPassword())) {
      throw new BadCredentialsException("");
    }
    return new UsernamePasswordAuthenticationToken(user, password);
  }

  public boolean supports(Class<?> arg0) {
    return true;
  }

}
