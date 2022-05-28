package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Category;
import com.bezkoder.springjwt.models.ProductHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {

  List<ProductHistory> findByNameContaining(String name);

  Page<ProductHistory> findByNameContaining(String name, Pageable pageable);

  List<ProductHistory> findAllByPrice(double price);

  Page<ProductHistory> findAllByPrice(double price, Pageable pageable);

  List<ProductHistory> findByNameContainingAndCategoryName(String name, String category);

  Page<ProductHistory> findByNameContainingAndCategory(String name, Category Category, Pageable pageable);

  void delete(ProductHistory product);
}
