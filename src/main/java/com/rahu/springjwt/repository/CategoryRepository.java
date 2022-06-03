package com.rahu.springjwt.repository;

import java.util.Optional;

import com.rahu.springjwt.models.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByName(String name);

  Optional<Category> findById(long id);

  // List<Product> ProductsbyCategory(String name);
}
