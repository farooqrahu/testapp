package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductSaleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSaleRepository extends JpaRepository<ProductSaleList, Long> {
  ProductSaleList findByProductOrderId(Long id);
}
