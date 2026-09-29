package com.mams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    java.util.Optional<User> findByUsername(String username);
}