package com.rahu.springjwt.repository;

import java.util.Optional;

import com.rahu.springjwt.models.ERole;
import com.rahu.springjwt.models.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
