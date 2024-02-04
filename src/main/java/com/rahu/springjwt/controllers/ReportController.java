package com.rahu.springjwt.controllers;

import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.security.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/report")
public class ReportController {

  @Autowired
  ReportService reportService;


  @PostMapping("/getProductReport")
  public ResponseEntity<?> getProductReport(@Valid @RequestBody ProductRequest productRequest) {
    return reportService.getProductReport(productRequest);
  }


}
