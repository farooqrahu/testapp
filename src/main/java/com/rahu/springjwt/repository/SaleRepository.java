package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<ProductSale, Long> {
  ProductSale findByProductOrderId(Long id);
}
