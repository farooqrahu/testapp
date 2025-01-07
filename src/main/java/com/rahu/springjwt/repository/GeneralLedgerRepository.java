package com.rahu.springjwt.repository;

import com.rahu.springjwt.models.GeneralLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralLedgerRepository extends JpaRepository<GeneralLedger, Long> {

  List<GeneralLedger> findByCustomerName(String name);

  List<GeneralLedger> findByCustomerId(Long id);

  @Query("select gl from GeneralLedger gl where gl.customer.id=?1 order by gl.createdAt desc ")
  List<GeneralLedger> findAllByCustomerId();

}
