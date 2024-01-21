package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductSaleList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
  @Query("select p from ProductOrder p order by p.createdAt desc ")
  List<ProductOrder> findAllByReturnedIsFalse();

  @Query("select p from ProductOrder p  order by p.createdAt desc ")
  Page<ProductOrder> findAllByReturnedIsFalse(Pageable pageable);

  @Query("select max(p.invoiceNo) from ProductOrder p ")
  Long findMaxInvoiceNo();

  @Query("select o from ProductOrder o where o.createdAt between ?1 and ?2")
  List<ProductOrder> findAllByNotReturned(Date startDate, Date endDate);

  @Query("select o from ProductOrder o ")
  List<ProductOrder> findAllByNotReturned();

}
