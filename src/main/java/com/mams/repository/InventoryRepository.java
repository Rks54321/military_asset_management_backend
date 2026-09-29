package com.mams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    java.util.Optional<Inventory> findByBaseIdAndEquipmentId(Long baseId, Long equipmentId);

    List<Inventory> findByBaseId(Long baseId);
}