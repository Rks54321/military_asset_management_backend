package com.mams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByBaseId(Long baseId);
}