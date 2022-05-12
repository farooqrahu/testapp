package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
  @Query("select p from ProductOrder p where p.isReturned=false")
  List<ProductOrder> findAllByReturnedIsFalse();
  @Query("select p from ProductOrder p where p.isReturned=false")
  Page<ProductOrder> findAllByReturnedIsFalse(Pageable pageable);
}
