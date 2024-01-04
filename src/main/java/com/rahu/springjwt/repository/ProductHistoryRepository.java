package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.Category;
import com.rahu.springjwt.models.ProductHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHistoryRepository extends PagingAndSortingRepository<ProductHistory, Long> {

  List<ProductHistory> findByNameContaining(String name);

  Page<ProductHistory> findByNameContaining(String name, Pageable pageable);

  List<ProductHistory> findAllByPrice(double price);

  Page<ProductHistory> findAllByPrice(double price, Pageable pageable);

  List<ProductHistory> findByNameContainingAndCategoryName(String name, String category);

  Page<ProductHistory> findByNameContainingOrCategory(String name, Category Category, Pageable pageable);

  void delete(ProductHistory product);
}
