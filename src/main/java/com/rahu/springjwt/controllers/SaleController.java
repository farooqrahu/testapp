package com.rahu.springjwt.controllers;

import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.payload.request.SaleRequestList;
import com.rahu.springjwt.security.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/sale")
public class SaleController {

  private final SaleService saleService;

  @Autowired
  public SaleController(SaleService saleService) {
    this.saleService = saleService;
  }

  @PostMapping("/submitSaleOrder")
  public ResponseEntity<?> submitSaleOrder(@Valid @RequestBody SaleRequestList productRequest) throws
    IOException {
    return saleService.submitSaleOrder(productRequest);

  }

  @PostMapping("/returnProductSale")
  public ResponseEntity<?> returnProductSale(@Valid @RequestBody SaleRequestList productRequest) throws
    IOException {
    return saleService.returnProductSale(productRequest);

  }

  @PostMapping("/findOrders")
  public ResponseEntity<?> findOrders(@Valid @RequestBody ProductRequest productRequest) {
    return saleService.findOrders(productRequest);
  }

  @PostMapping("/findReturnOrdersByInvoiceNo")
  public ResponseEntity<?> findReturnOrdersByInvoiceNo(@Valid @RequestBody ProductRequest productRequest) {
    return saleService.findReturnOrdersByInvoiceNo(productRequest);
  }
  @PostMapping("/findCustomerByMobileNumber")
  public ResponseEntity<?> findCustomerByMobileNumber(@Valid @RequestBody SaleRequestList productRequest) {
    return saleService.findCustomerByMobileNumber(productRequest);
  }

}
