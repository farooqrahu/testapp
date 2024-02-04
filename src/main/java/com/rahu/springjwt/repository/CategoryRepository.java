package com.rahu.springjwt.repository;

import java.util.List;
import java.util.Optional;

import com.rahu.springjwt.models.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByName(String name);

  Optional<Category> findById(long id);

  @Query("select c from Category c order by c.createdAt desc ")
  List<Category> findAllByCreateAt();

  // List<Product> ProductsbyCategory(String name);
}
