package com.mams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}