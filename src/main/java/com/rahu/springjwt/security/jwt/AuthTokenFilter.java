package com.rahu.springjwt.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rahu.springjwt.security.services.UserDetailsImpl;
import com.rahu.springjwt.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//      throws ServletException, IOException {
//    try {
//      String jwt = parseJwt(request);
//      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//        String username = jwtUtils.getUserNameFromJwtToken(jwt);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
//            userDetails.getAuthorities());
//        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//      }
//    } catch (Exception e) {
//      logger.error("Cannot set user authentication: {}", e);
//    }
//
//    filterChain.doFilter(request, response);
//  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {
    final String requestTokenHeader = request.getHeader("Authorization");
    String username = null;
    String jwtToken = null;
    Claims claims = new DefaultClaims();

    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      try {
        username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        claims = jwtUtils.getAllClaimsFromToken(jwtToken);
      } catch (IllegalArgumentException e) {
        System.out.println("Unable to get JWT Token");
      }
    } else if (requestTokenHeader == null) {
      logger.info("Does not provide Authorization Header");
    } else if (!requestTokenHeader.startsWith("Bearer ")) {
      logger.warn("JWT Token does not begin with Bearer");
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetailsImpl userLoginDetails = this.userDetailsService.loadUserByUsername(username);
      // the following validates the tokens,
      // compares the jwt_sign from the claim and the one from DB
      // also makes sure the user is logged in
      if ((jwtUtils.validateToken(jwtToken, userLoginDetails) && userLoginDetails.getJwtSign()!=null ) && userLoginDetails.getJwtSign().equals(claims.get("jwt_sign")) && userLoginDetails.isActive()) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
          userLoginDetails, null, userLoginDetails.getAuthorities());
        usernamePasswordAuthenticationToken
          .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    chain.doFilter(request, response);
  }
  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }

    return null;
  }
}
