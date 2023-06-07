package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<ProductReturn, Long> {
    Optional<ProductReturn> findByInvoiceNo(Long invoiceNo);
}
