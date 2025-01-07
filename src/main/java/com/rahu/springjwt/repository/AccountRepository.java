package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  // You can define custom query methods here if needed

  // Example: Find by name
  Optional<Account> findByName(String name);
}
