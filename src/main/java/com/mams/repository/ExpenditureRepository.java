package com.mams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByBaseId(Long baseId);
}