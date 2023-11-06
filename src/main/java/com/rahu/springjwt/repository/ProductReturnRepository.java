package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductReturn;
import com.rahu.springjwt.models.ProductReturnList;
import com.rahu.springjwt.models.ProductSaleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReturnRepository extends JpaRepository<ProductReturnList, Long> {
//  ProductReturnList findByProductReturnId(Long id);
}
