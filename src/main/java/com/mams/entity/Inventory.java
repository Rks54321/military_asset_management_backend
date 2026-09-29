package com.mams.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"base_id", "equipment_id"}))
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    public Base base;

    @ManyToOne(optional = false)
    public Equipment equipment;

    public int openingBalance;

    public int quantity;

    public Inventory() {
    }

    public Inventory(Base b, Equipment e, int opening, int qty) {
        base = b;
        equipment = e;
        openingBalance = opening;
        quantity = qty;
    }
}