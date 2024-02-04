package com.rahu.springjwt.repository;

import java.util.List;
import java.util.Optional;

import com.rahu.springjwt.models.ShoppingCart;
import com.rahu.springjwt.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
  Optional<ShoppingCart> findById(long id);

  List<ShoppingCart> findByCartItemsIsNotEmpty();

  Optional<ShoppingCart> findByUser(User user);

}
