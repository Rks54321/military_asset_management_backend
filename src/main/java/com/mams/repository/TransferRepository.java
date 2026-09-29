package com.mams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByFromBaseIdOrToBaseId(Long fromId, Long toId);
}