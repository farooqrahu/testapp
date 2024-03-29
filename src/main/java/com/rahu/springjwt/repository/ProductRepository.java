package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.Category;
import com.rahu.springjwt.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByNameContaining(String name);

  List<Product> findByNameContainingAndOutOfStock(String name, Boolean outOfStock);

  Page<Product> findByNameContaining(String name, Pageable pageable);

  @Query("select p from Product p where  UPPER(p.name) like %:productName% and  p.outOfStock=FALSE")
  Page<Product> findByNameInStock(String productName, Pageable pageable);

  List<Product> findByNameContainingAndCategoryName(String name, String category);

  List<Product> findByNameContainingAndCategoryNameAndOutOfStock(String name, String category, Boolean outOfStock);

  @Query("select p from Product p where  UPPER(p.name) like %:productName% order by p.createdAt")
  Page<Product> findByProductName(String productName, Pageable pageable);

  Page<Product> findByNameContainingAndCategoryAndOutOfStock(String name, Category Category, Boolean outOfStock, Pageable pageable);


  void delete(Product product);

  @Query("select p from Product p order by p.createdAt desc ")
  List<Product> findAllByCreateDate();

  @Query("select p from Product p where p.outOfStock=?1")
  List<Product> findAllOutOfStock(Boolean outOfStock);

  @Query("select p from Product p where p.outOfStock=?1")
  Page<Product> findAllOutOfStock(Boolean outOfStock, Pageable paging);
}
