package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ConfirmationToken;
import com.bezkoder.springjwt.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

  ConfirmationToken findByConfirmationToken(String confirmationToken);

  ConfirmationToken findByUser(User user);

  @Modifying
  @Transactional
  @Query("delete from ConfirmationToken c where c.user.id=?1")
  void deleteByUserId(Long id);
}
