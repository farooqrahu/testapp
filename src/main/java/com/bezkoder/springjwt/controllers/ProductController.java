package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.repository.CategoryRepository;
import com.bezkoder.springjwt.repository.ProductRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.ProductService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/product")
public class ProductController {
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  CategoryRepository categoryRepository;
  @Autowired
  JwtUtils jwtUtils;
  @Autowired
  UserDetailsServiceImpl userDetailsServiceImpl;
  @Autowired
  ProductService productService;

  @RequestMapping(value = "/updateProductimage", consumes = {"multipart/mixed", "multipart/form-data"})
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public ResponseEntity<?> saveProductImage(@Valid @RequestPart("image") MultipartFile image,
                                            @Valid @RequestParam("number") int number, @Valid @RequestParam("id") long id) {
    return productService.saveProductImage(id, image, number);
  }

  @PostMapping("/findProduct")
  public ResponseEntity<?> findProduct(@Valid @RequestBody ProductRequest productRequest) {
    return productService.findProduct(productRequest);
  }

  @PostMapping("/addProduct")
  public ResponseEntity<?> addProduct(@Valid @RequestBody ProductRequest productRequest) throws
     IOException {
    return productService.addProduct(productRequest);

  }

  @PostMapping("/updateProduct")
  public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductRequest productRequest) throws IOException {
    return productService.updateProduct(productRequest);

  }

  @PostMapping("/deleteProduct")
  public ResponseEntity<?> deleteProduct(@Valid @RequestBody ProductRequest productRequest) {
    return productService.deleteProduct(productRequest);

  }

  @PostMapping("/addCategory")
  public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest categoryRequest) {

    return productService.addCategory(categoryRequest);
  }

  @PostMapping("/deleteCategory")
  public ResponseEntity<?> deleteCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
    return productService.deleteCategory(categoryRequest);
  }

  @PostMapping("/updateCategory")
  public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
    return productService.updateCategory(categoryRequest);
  }


  @PostMapping("/addCompany")
  public ResponseEntity<?> addCompany(@Valid @RequestBody CompanyRequest companyRequest) {

    return productService.addCompany(companyRequest);
  }

  @PostMapping("/deleteCompany")
  public ResponseEntity<?> deleteCompany(@Valid @RequestBody CompanyRequest companyRequest) {
    return productService.deleteCompany(companyRequest);
  }

  @PostMapping("/updateCompany")
  public ResponseEntity<?> updateCompany(@Valid @RequestBody CompanyRequest companyRequest) {
    return productService.updateCompany(companyRequest);
  }

  @PostMapping("/getAllCategories")
  public ResponseEntity<?> getAllCategories(@Valid @RequestBody CategoryRequest categoryRequest) {
    return productService.getAllCategories(categoryRequest);
  }

  @PostMapping("/getAllCompanies")
  public ResponseEntity<?> getAllCompanies(@Valid @RequestBody CompanyRequest companyRequest) {
    return productService.getAllCompanies(companyRequest);
  }

  // shopping cart
  @PostMapping("/addtocart")
  public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest cartRequest) {
    return productService.addToCart(cartRequest);
  }

  @PostMapping("/getAllShoppingCarts")
  public ResponseEntity<?> getAllShoppingCarts(@Valid @RequestBody CartRequest cartRequest) {
    return productService.getAllShoppingCarts(cartRequest);
  }

  @PostMapping("/getShoppingCart")
  public ResponseEntity<?> getShoppingcart(@Valid @RequestBody CartRequest cartRequest) {
    return productService.getShoppingcart(cartRequest);
  }

  @PostMapping("/setShippingDate")
  public ResponseEntity<?> setShippingDate(@Valid @RequestBody ShoppingCartRequest shoppingcartRequest) {
    return productService.setShippingDate(shoppingcartRequest);
  }

  @PostMapping("/setCompletedDate")
  public ResponseEntity<?> setCompletedDate(@Valid @RequestBody ShoppingCartRequest shoppingcartRequest) {
    return productService.setCompletedDate(shoppingcartRequest);
  }

  @PostMapping("/removeCartItem")
  public ResponseEntity<?> removeCartItem(@Valid @RequestBody CartRequest cartRequest) {
    return productService.removeCartItem(cartRequest);
  }

  @PostMapping("/updateShoppingCartItems")
  public ResponseEntity<?> updateShoppingCartItems(@Valid @RequestBody CartItemRequest cartItemRequest) {
    return productService.updateShoppingCartItems(cartItemRequest);
  }

}
