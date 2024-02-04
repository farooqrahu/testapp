package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByMobileNumber(String mobileNumber);
  @Query("select  c from Customer c")
  List<Customer> getAllCustomers();
}
