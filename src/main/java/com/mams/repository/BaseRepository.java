package com.mams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Base;

public interface BaseRepository extends JpaRepository<Base, Long> {
}
