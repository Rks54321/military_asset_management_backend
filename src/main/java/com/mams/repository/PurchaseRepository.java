package com.mams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByBaseId(Long baseId);
}