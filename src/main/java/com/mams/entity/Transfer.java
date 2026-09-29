package com.mams.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    public Base fromBase;

    @ManyToOne(optional = false)
    public Base toBase;

    @ManyToOne(optional = false)
    public Equipment equipment;

    public int quantity;
    public String reference;
    public LocalDateTime transferredAt;
}