package com.rahu.springjwt.security.services;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.rahu.springjwt.models.*;
import com.rahu.springjwt.payload.request.LoginRequest;
import com.rahu.springjwt.payload.request.SignupRequest;
import com.rahu.springjwt.payload.request.UpdateRequest;
import com.rahu.springjwt.payload.response.MessageResponse;
import com.rahu.springjwt.payload.response.UserListResponse;
import com.rahu.springjwt.payload.response.UserResponse;
import com.rahu.springjwt.repository.ConfirmationTokenRepository;
import com.rahu.springjwt.repository.FileDBRepository;
import com.rahu.springjwt.repository.RoleRepository;
import com.rahu.springjwt.repository.UserRepository;
import com.rahu.springjwt.security.CustomAuthenticationProvider;
import com.rahu.springjwt.security.jwt.JwtUtils;

import com.rahu.springjwt.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  CustomAuthenticationProvider authenticationProvider;
  @Autowired
  JwtUtils jwtUtils;
  @Autowired
  UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired
  PasswordEncoder encoder;
  @Autowired
  UserRepository userRepository;
  @Autowired
  private FileDBRepository fileDBRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  ProductService productService;
  @Autowired
  ConfirmationTokenRepository confirmationTokenRepository;
  @Autowired
  EmailSenderService emailSenderService;
  @Value("${upload.path}")
  private String uploadPath;
  @Autowired
  private JwtUtils jwtToken;

  @Override
  @Transactional
  public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    return UserDetailsImpl.build(user);

  }



  public ResponseEntity<?> getAllUsers() {
    checkAdmin();
    List<String> roles = getRoles();
    if (roles.contains("ROLE_ADMIN")) {
      return ResponseEntity.ok(UserListResponse.userResponseFactory(userRepository.findAll()));
    } else {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Unauthorized access"));
    }
  }

  public ResponseEntity<?> deleteUser(UpdateRequest updateRequest) {
    checkAdminOrConcernedUser(updateRequest.getId());

    UserDetailsImpl userDetails = getUserDetails();
    List<String> roles = getRoles();

    if (roles.contains("ROLE_ADMIN") || updateRequest.getId() == userDetails.getId()) {
      confirmationTokenRepository.deleteByUserId(updateRequest.getId());
      userRepository.deleteById(updateRequest.getId());
      return ResponseEntity.ok(new MessageResponse("User deleted"));
    } else {
      return ResponseEntity.badRequest().body(new MessageResponse("Error deleting user: Unauthorized access"));
    }
  }

  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) throws Exception {
    authenticate(loginRequest.getUsername(),loginRequest.getPassword());
    Optional<User> user = userRepository.findByUsernameIgnoreCase(loginRequest.getUsername());
    if (user.isEmpty() || Objects.equals(user.get().getStatus(), "Deleted"))
      return ResponseEntity.badRequest().body(new MessageResponse("Error:Account does not exist"));
    if (Objects.equals(user.get().getStatus(), "Banned"))
      return ResponseEntity.badRequest().body(new MessageResponse("Error:Account Banned"));
    updateJwtSign(user);

    final UserDetailsImpl userDetails = loadUserByUsername(loginRequest.getUsername());
    final String token = jwtToken.generateToken(userDetails);
//    user = userRepository.findById(userDetails.getId());
    if (user.get().getStatus().equals("Activation pending")) {
      SignupRequest signupRequest = new SignupRequest();
      signupRequest.setUsername(loginRequest.getUsername());
      signupRequest.setPassword(loginRequest.getPassword());
      this.sendNewConfirmationToken(signupRequest);
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Account not verified, please check your email for a new activation link"));
    }

    if (!user.get().getStatus().equals("Activated"))
      return ResponseEntity.badRequest().body(new MessageResponse("Error: please verify your account"));
    return ResponseEntity.ok(UserResponse.userResponseFactory(user.get(),token));
  }
  /**
   * the following method is used to authenticate the given user
   *
   * @param username
   * @param password
   * @throws Exception
   */
  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new DisabledException("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException("INVALID_CREDENTIALS", e);
    }
  }
  public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {

    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }
    if (signUpRequest.getPassword().length() < 6)
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Password must not be empty or less than 6 characters"));

    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));
    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
    roles.add(userRole);
    user.setRoles(roles);
    user.setStatus("Activated");
    userRepository.save(user);

    return sendNewConfirmationToken(signUpRequest);
  }

  public ResponseEntity<?> sendNewConfirmationToken(SignupRequest signUpRequest) {
    User user = userRepository.findByUsernameIgnoreCase(signUpRequest.getUsername()).get();
    if (user == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user does not exist"));
    ConfirmationToken token = confirmationTokenRepository.findByUser(user);
    if (token != null)
      confirmationTokenRepository.deleteById(token.getId());
    ConfirmationToken confirmationToken = new ConfirmationToken(user);
    confirmationTokenRepository.save(confirmationToken);
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getEmail());
    mailMessage.setSubject("Complete Registration!");
    mailMessage.setFrom("farahu008@gmail.com");
    mailMessage.setText("To activate your account, please click here : " + "http://localhost:4200/#/login?token="
        + confirmationToken.getConfirmationToken());
//    emailSenderService.sendEmail(mailMessage);

    return ResponseEntity
        .ok(new MessageResponse("verification link sent, please check your email to activate your account"));

  }

  public ResponseEntity<?> confirmUserAccount(String confirmationToken) {

    if (confirmationToken == null || confirmationToken == "")
      return ResponseEntity.badRequest().body(new MessageResponse("invalid token"));
    ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
    if (token != null) {
      User user = userRepository.findById(token.getUser().getId()).get();
      if ((user.getStatus() == "Deleted") || user == null)
        return ResponseEntity.ok(new MessageResponse("Account does not exist"));
      if (user.getStatus() == "Activated")
        return ResponseEntity.ok(new MessageResponse("Account already activated"));
      user.setStatus("Activated");
      userRepository.save(user);
      confirmationTokenRepository.deleteById(token.getId());
      return ResponseEntity.ok(new MessageResponse("Account Activated"));
    }
    return ResponseEntity.badRequest().body(new MessageResponse("Error Activating Account"));
  }

  public ResponseEntity<?> getUserInfo() {
    UserDetailsImpl userDetails = getUserDetails();
    User user = userRepository.findById(userDetails.getId()).get();
    return ResponseEntity.ok(UserResponse.userResponseFactory(user,null));

  }
  public User getUser() {
    UserDetailsImpl userDetails = getUserDetails();
    return userRepository.findById(userDetails.getId()).get();
  }

  public ResponseEntity<?> updateUser(UpdateRequest updateRequest) {
    checkAdminOrConcernedUser(updateRequest.getId());
    UserDetailsImpl userDetails = getUserDetails();
    User user = userDetailsServiceImpl.saveUser(userDetails.getId(), updateRequest);
    return ResponseEntity.ok(UserResponse.userResponseFactory(user,null));

  }

  public User saveUser(long id, UpdateRequest updateRequest) {
    userDetailsServiceImpl.checkAdminOrConcernedUser(id);
    User user = userRepository.findById(id).get();
    if (updateRequest.getEmail() != null)
      user.setEmail(updateRequest.getEmail());
    if (updateRequest.getPassword() != null)
      user.setPassword(encoder.encode(updateRequest.getPassword()));
    user.setUsername(updateRequest.getUsername());
    user.setAddress(updateRequest.getAddress());
    user.setAge(updateRequest.getAge());
    user.setCity(updateRequest.getCity());
    user.setCountry(updateRequest.getCountry());
    user.setDescription(updateRequest.getDescription());
    user.setJob(updateRequest.getJob());
    user.setName(updateRequest.getName());
    user.setSurname(updateRequest.getSurname());
    return userRepository.save(user);
  }

  public void saveUserProfileImage(long id, MultipartFile image) {

    User user = userRepository.findById(id).get();
    try {
      String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
      FileDB fileDB = new FileDB(fileName, image.getContentType(), image.getBytes());
      user.setImage(true);
      user.setFiles(fileDB);
      fileDBRepository.save(fileDB);
    } catch (IOException e) {
      e.printStackTrace();
    }
    userRepository.save(user);
  }

  public ResponseEntity<?> updateUserProfilePicture(MultipartFile image) {
    UserDetailsImpl userDetails = getUserDetails();
    this.saveUserProfileImage(userDetails.getId(), image);
    return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
  }

  public Authentication isAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public UserDetailsImpl getUserDetails() {
    Authentication authentication = isAuthenticated();
    return (UserDetailsImpl) authentication.getPrincipal();
  }

  public List<String> getRoles() {
    UserDetailsImpl userDetails = getUserDetails();
    return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
  }



  public boolean isAdmin() {
    return userDetailsServiceImpl.getRoles().contains(("ROLE_ADMIN"));
  }

  /**
   * the following method is used to update the jwt sign
   *
   * @param
   * @param jwt
   */
  public void updateJwtSign(Optional<User> jwt) {
    jwt.get().setJwtSign(String.valueOf(System.currentTimeMillis()));
    this.userRepository.updateJwtSign(jwt.get().getJwtSign(), jwt.get().getId());
  }

  public ResponseEntity<?> notAuthorizedError() {
    return ResponseEntity.badRequest().body(new MessageResponse("Error: Unauthorized"));
  }

  public ResponseEntity<?> userNotFound() {
    return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
  }

  public void checkAdmin() {
    if (!isAdmin())
      notAuthorizedError();
  }

  public void checkAdminOrConcernedUser(Long userid) {
    Optional<User> user = userRepository.findById(userid);
    if (user.isEmpty())
      userNotFound();
    if (!isAdmin() || !Objects.equals(user.get().getId(), userid))
      notAuthorizedError();
  }
}
