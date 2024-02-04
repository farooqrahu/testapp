package com.rahu.springjwt.security.services;

import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductSaleList;
import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.payload.response.ProductResponse;
import com.rahu.springjwt.payload.response.ReportResponse;
import com.rahu.springjwt.repository.ProductOrderRepository;
import com.rahu.springjwt.repository.ProductSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ReportService {
  @Autowired
  ProductSaleRepository saleRepository;

  public ResponseEntity<?> getProductReport(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if(productRequest.getName()!=null && !"".equals(productRequest.getName())){
      return ResponseEntity.ok(new ReportResponse(saleRepository.findByProductName(productRequest.getName().toUpperCase(Locale.ROOT), paging)));
    }else{
      return ResponseEntity.ok(new ReportResponse(saleRepository.findAll(paging)));
    }
  }
  public Pageable checkPaging(ProductRequest productRequest) {
    if (productRequest.getPagesize() > 0 && productRequest.getPagenumber() >= 0) {
      if (productRequest.getSortdirection().equals("desc")) {
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
