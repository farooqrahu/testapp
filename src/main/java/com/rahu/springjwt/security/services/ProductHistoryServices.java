package com.rahu.springjwt.security.services;

import com.rahu.springjwt.dto.ProductHistoryDto;
import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.payload.response.ProductHistoryResponse;
import com.rahu.springjwt.repository.ProductHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Service
public class ProductHistoryServices {

  @Autowired
  private ProductHistoryRepository productHistoryRepository;


  public ResponseEntity<?> findProductHistory(ProductRequest productRequest) {
    if (productRequest.getCategory() != null && productRequest.getName() != null) {
      return findByNameContainingAndCategory(productRequest);
    } else {
      if (productRequest.getName() != null) {
        return findByNameContaining(productRequest);
      } else {
        return findAllProducts(productRequest);
      }
    }
  }

  public ResponseEntity<?> findAllProducts(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null)
      return ResponseEntity.ok(new ProductHistoryResponse(productHistoryRepository.findAll().stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList())));
    return ResponseEntity.ok(new ProductHistoryResponse(productHistoryRepository.findAll(paging).stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList())));
  }

  public ResponseEntity<?> findByNameContaining(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null)
      return ResponseEntity.ok(new ProductHistoryResponse(productHistoryRepository.findByNameContaining(productRequest.getName()).stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList())));
    else
      return ResponseEntity
        .ok(new ProductHistoryResponse(productHistoryRepository.findByNameContaining(productRequest.getName(), paging).stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList())));
  }

  public ResponseEntity<?> findByNameContainingAndCategory(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);


    if (paging == null)
      return ResponseEntity.ok(new ProductHistoryResponse(
        productHistoryRepository.findByNameContainingAndCategoryName(productRequest.getName(), productRequest.getCategory().getName()).stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList())));
    else
      return ResponseEntity.ok(new ProductHistoryResponse(productHistoryRepository
        .findByNameContainingAndCategory(productRequest.getName(), productRequest.getCategory(), paging).stream().map(ProductHistoryDto::factoryProduct).collect(Collectors.toList())));
  }
  public Pageable checkPaging(ProductRequest productRequest) {
    if (productRequest.getPagesize() > 0 && productRequest.getPagenumber() >= 0) {
      if (productRequest.getSortdirection() == "desc") {
        return PageRequest.of(productRequest.getPagenumber(), productRequest.getPagesize(),
          Sort.by(productRequest.getSort()).descending());
      } else {
        return PageRequest.of(productRequest.getPagenumber(), productRequest.getPagesize(),
          Sort.by(productRequest.getSort()).ascending());
      }
    }
    return null;
  }

}
