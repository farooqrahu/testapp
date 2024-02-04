package com.rahu.springjwt.security.services;

import com.rahu.springjwt.dto.CustomerDto;
import com.rahu.springjwt.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerService {
  private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

  @Autowired
  private CustomerRepository customerRepository;


  public ResponseEntity<?> getAllCustomers() {
    List<CustomerDto> list = customerRepository.getAllCustomers().stream().map(CustomerDto::factoryCustomerDto).filter(Objects::nonNull).collect(Collectors.toList());
    return ResponseEntity.ok(list);
  }

}
