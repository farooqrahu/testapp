package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Category;
import com.bezkoder.springjwt.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  Optional<Category> findByName(String name);

  Optional<Category> findById(long id);

  // List<Product> ProductsbyCategory(String name);
}
