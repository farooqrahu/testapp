package com.rahu.springjwt.controllers;

import com.rahu.springjwt.dto.DashboardDto;
import com.rahu.springjwt.security.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/dashboard")
public class DashboardController {

  private final SaleService saleService;

  @Autowired
  public DashboardController(SaleService saleService) {
    this.saleService = saleService;
  }

  @GetMapping("/totalSales")
  public DashboardDto totalSales() {
    return saleService.totalSales();
  }

}
