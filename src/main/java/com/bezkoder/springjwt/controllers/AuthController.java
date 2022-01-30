package com.bezkoder.springjwt.controllers;

import javax.validation.Valid;

import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.request.UpdateRequest;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  UserDetailsServiceImpl userDetailsServiceImpl;

  @PostMapping("/getAllUsers")
  public ResponseEntity<?> getAllUsers(@Valid @RequestBody LoginRequest loginRequest) {
    return userDetailsServiceImpl.getAllUsers(loginRequest);

  }

  @PostMapping("/deleteUser")
  public ResponseEntity<?> deleteUser(@Valid @RequestBody UpdateRequest updateRequest) {

    return userDetailsServiceImpl.deleteUser(updateRequest);
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {

    return userDetailsServiceImpl.authenticateUser(loginRequest);
  }

  @PostMapping("/user-profile")
  public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateRequest updateRequest) {

    return userDetailsServiceImpl.updateUser(updateRequest);
  }

  @GetMapping("/user-profile")
  public ResponseEntity<?> getUserInfo(@Valid @RequestBody UpdateRequest updateRequest) {
    return userDetailsServiceImpl.getUserInfo(updateRequest);
  }

  @GetMapping("/user-profile{test}")
  public ResponseEntity<?> getTest(@PathVariable String test) {
  System.out.println(test);
    return null;
  }
    @RequestMapping("/updateProfileImage")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public ResponseEntity<?> updateUserProfilePicture(@RequestPart("image") MultipartFile image,
      @RequestPart("username") String username, @RequestPart("password") String password) {

    return userDetailsServiceImpl.updateUserProfilePicture(image, username, password);

  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return userDetailsServiceImpl.registerUser(signUpRequest);
  }

  @GetMapping("/confirmUserAccount")
  public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
    return userDetailsServiceImpl.confirmUserAccount(confirmationToken);
  }
}
