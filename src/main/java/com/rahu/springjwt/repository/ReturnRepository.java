package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<ProductReturn, Long> {
}
