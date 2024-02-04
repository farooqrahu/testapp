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

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductHistoryServices {

  @Autowired
  private ProductHistoryRepository productHistoryRepository;


  public ResponseEntity<?> findProductHistory(ProductRequest productRequest) {
    if (productRequest.getCategory() != null || (!"".equals(productRequest.getName()) && productRequest.getName() != null)) {
      return findByNameContainingAndCategory(productRequest);
    } else {
      return findAllProducts(productRequest);
    }
//    }
  }

  public ResponseEntity<?> findAllProducts(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    return ResponseEntity.ok(new ProductHistoryResponse(productHistoryRepository.findAll(paging)));
  }

  public ResponseEntity<?> findByNameContainingAndCategory(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    return ResponseEntity.ok(new ProductHistoryResponse(productHistoryRepository
      .findByNameContainingOrCategory(productRequest.getName(), productRequest.getCategory(), paging)));
  }

  public Pageable checkPaging(ProductRequest productRequest) {
    if (productRequest.getPagesize() > 0 && productRequest.getPagenumber() >= 0) {
      if (Objects.equals(productRequest.getSortdirection(), "desc")) {
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
