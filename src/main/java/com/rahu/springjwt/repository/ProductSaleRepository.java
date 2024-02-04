package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductSaleList;
import com.rahu.springjwt.payload.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSaleRepository extends JpaRepository<ProductSaleList, Long> {
  ProductSaleList findByProductOrderId(Long id);

  @Query("select p from ProductSaleList p where UPPER(p.product.name) like %:name%")
  Page<ProductSaleList> findByProductName(String name, Pageable pageable);

  @Query("select p from ProductSaleList p where p.productOrder.isReturned=false")
  Page<ProductSaleList> findAll(Pageable pageable);
}
