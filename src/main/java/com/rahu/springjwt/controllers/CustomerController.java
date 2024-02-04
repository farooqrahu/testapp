package com.rahu.springjwt.controllers;

import com.rahu.springjwt.security.services.CustomerService;
import com.rahu.springjwt.security.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/customer")
public class CustomerController {

  private final CustomerService saleService;

  @Autowired
  public CustomerController(CustomerService saleService) {
    this.saleService = saleService;
  }

  @GetMapping("/getAllCustomers")
  public ResponseEntity<?> getAllCustomers() {
    return saleService.getAllCustomers();
  }

}
