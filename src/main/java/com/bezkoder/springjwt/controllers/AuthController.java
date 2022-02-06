package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.request.UpdateRequest;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  UserDetailsServiceImpl userDetailsServiceImpl;

  @PostMapping("/getAllUsers")
  public ResponseEntity<?> getAllUsers() {
    return userDetailsServiceImpl.getAllUsers();
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
  public ResponseEntity<?> getUserInfo() {
    return userDetailsServiceImpl.getUserInfo();
  }

    @RequestMapping("/updateProfileImage")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public ResponseEntity<?> updateUserProfilePicture(@RequestPart("image") MultipartFile image) {
    return userDetailsServiceImpl.updateUserProfilePicture(image);
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return userDetailsServiceImpl.registerUser(signUpRequest);
  }

  @GetMapping("/confirmUserAccount")
  public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
    return userDetailsServiceImpl.confirmUserAccount(confirmationToken);
  }


//  public static void main(String[] args) throws FileNotFoundException, DocumentException {
//
//    Document document = new Document(new Rectangle(PageSize.A4));
//    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("d:/Java4s_BarCode_128.pdf"));
//
//    document.open();
//    document.add(new Paragraph("Khalil Cloth Shop"));
//
//    Barcode128 code128 = new Barcode128();
//    code128.setGenerateChecksum(true);
//    code128.setCode("RS: 140.00");
//    code128.setBarHeight(20);
//    code128.setBaseline(10);
//    document.add(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
//    document.close();
//    System.out.println("Document Generated...!!!!!!");
//  }


}
