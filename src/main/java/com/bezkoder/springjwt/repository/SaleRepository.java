package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ProductSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<ProductSale, Long> {
  ProductSale findByProductOrderId(Long id);
}
