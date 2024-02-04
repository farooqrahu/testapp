package com.rahu.springjwt.repository;

import java.util.Optional;

import com.rahu.springjwt.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmailIgnoreCase(String email);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Optional<User> findByUsernameIgnoreCase(String username);
  /**
   * the following method is used to update the jwt sign of the respective user
   *
   * @param jwtSign
   * @param id
   */
  @Modifying
  @Transactional
  @Query("update User set jwtSign = :jwt_sign where id = :id")
  void updateJwtSign(@Param("jwt_sign") String jwtSign, @Param("id") Long id);

}
