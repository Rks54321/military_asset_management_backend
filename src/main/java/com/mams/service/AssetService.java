package com.mams.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mams.entity.Assignment;
import com.mams.entity.AuditLog;
import com.mams.entity.Expenditure;
import com.mams.entity.Inventory;
import com.mams.entity.Purchase;
import com.mams.entity.Transfer;
import com.mams.repository.AssignmentRepository;
import com.mams.repository.AuditLogRepository;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentRepository;
import com.mams.repository.ExpenditureRepository;
import com.mams.repository.InventoryRepository;
import com.mams.repository.PurchaseRepository;
import com.mams.repository.TransferRepository;
import com.mams.repository.UserRepository;

@Service
public class AssetService {

    final InventoryRepository inv;
    final BaseRepository bases;
    final EquipmentRepository eq;
    final PurchaseRepository purchases;
    final TransferRepository transfers;
    final AssignmentRepository assignments;
    final ExpenditureRepository expenditures;
    final AuditLogRepository logs;
    final UserRepository users;

    public AssetService(
            InventoryRepository i,
            BaseRepository b,
            EquipmentRepository e,
            PurchaseRepository p,
            TransferRepository t,
            AssignmentRepository a,
            ExpenditureRepository x,
            AuditLogRepository l,
            UserRepository u) {

        inv = i;
        bases = b;
        eq = e;
        purchases = p;
        transfers = t;
        assignments = a;
        expenditures = x;
        logs = l;
        users = u;
    }

    Inventory inventory(Long b, Long e) {
        return inv.findByBaseIdAndEquipmentId(b, e)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    void audit(String action, String type, Long id, String details, String username) {
        AuditLog l = new AuditLog();
        l.action = action;
        l.entityType = type;
        l.entityId = id;
        l.details = details;
        l.createdAt = LocalDateTime.now();
        users.findByUsername(username).ifPresent(x -> l.performedBy = x);
        logs.save(l);
    }

    @Transactional
    public Purchase purchase(Long b, Long e, int q, String ref, String user) {
        if (q <= 0)
            throw new RuntimeException("Quantity must be positive");

        var i = inventory(b, e);
        i.quantity += q;
        inv.save(i);

        Purchase p = new Purchase();
        p.base = bases.findById(b).orElseThrow();
        p.equipment = eq.findById(e).orElseThrow();
        p.quantity = q;
        p.reference = ref;
        p.purchasedAt = LocalDateTime.now();
        purchases.save(p);

        audit("PURCHASE", "Purchase", p.id, "Base " + b + " quantity " + q, user);

        return p;
    }

    @Transactional
    public Transfer transfer(Long from, Long to, Long e, int q, String ref, String user) {
        if (from.equals(to))
            throw new RuntimeException("Bases must be different");

        var src = inventory(from, e);

        if (src.quantity < q)
            throw new RuntimeException("Insufficient stock");

        var dst = inventory(to, e);

        src.quantity -= q;
        dst.quantity += q;

        inv.save(src);
        inv.save(dst);

        Transfer t = new Transfer();
        t.fromBase = bases.findById(from).orElseThrow();
        t.toBase = bases.findById(to).orElseThrow();
        t.equipment = eq.findById(e).orElseThrow();
        t.quantity = q;
        t.reference = ref;
        t.transferredAt = LocalDateTime.now();
        transfers.save(t);

        audit("TRANSFER", "Transfer", t.id, "From " + from + " to " + to + " quantity " + q, user);

        return t;
    }

    @Transactional
    public Assignment assign(Long b, Long e, String person, int q, String user) {
        var i = inventory(b, e);

        if (i.quantity < q)
            throw new RuntimeException("Insufficient stock");

        i.quantity -= q;
        inv.save(i);

        Assignment a = new Assignment();
        a.base = bases.findById(b).orElseThrow();
        a.equipment = eq.findById(e).orElseThrow();
        a.personnelName = person;
        a.quantity = q;
        a.assignedAt = LocalDateTime.now();
        assignments.save(a);

        audit("ASSIGNMENT", "Assignment", a.id, "Personnel " + person + " quantity " + q, user);

        return a;
    }

    @Transactional
    public Expenditure expend(Long b, Long e, int q, String reason, String user) {
        var i = inventory(b, e);

        if (i.quantity < q)
            throw new RuntimeException("Insufficient stock");

        i.quantity -= q;
        inv.save(i);

        Expenditure x = new Expenditure();
        x.base = bases.findById(b).orElseThrow();
        x.equipment = eq.findById(e).orElseThrow();
        x.quantity = q;
        x.reason = reason;
        x.expendedAt = LocalDateTime.now();
        expenditures.save(x);

        audit("EXPENDITURE", "Expenditure", x.id, "Reason " + reason + " quantity " + q, user);

        return x;
    }

    public Map<String, Object> dashboard(Long baseId, Long equipmentId) {
        var list = baseId == null ? inv.findAll() : inv.findByBaseId(baseId);

        if (equipmentId != null)
            list = list.stream()
                    .filter(x -> x.equipment.id.equals(equipmentId))
                    .toList();

        int opening = list.stream()
                .mapToInt(x -> x.openingBalance)
                .sum();

        int closing = list.stream()
                .mapToInt(x -> x.quantity)
                .sum();

        int pur = purchases.findAll().stream()
                .filter(x -> baseId == null || x.base.id.equals(baseId))
                .filter(x -> equipmentId == null || x.equipment.id.equals(equipmentId))
                .mapToInt(x -> x.quantity)
                .sum();

        int tin = transfers.findAll().stream()
                .filter(x -> baseId == null || x.toBase.id.equals(baseId))
                .filter(x -> equipmentId == null || x.equipment.id.equals(equipmentId))
                .mapToInt(x -> x.quantity)
                .sum();

        int tout = transfers.findAll().stream()
                .filter(x -> baseId == null || x.fromBase.id.equals(baseId))
                .filter(x -> equipmentId == null || x.equipment.id.equals(equipmentId))
                .mapToInt(x -> x.quantity)
                .sum();

        int assigned = assignments.findAll().stream()
                .filter(x -> baseId == null || x.base.id.equals(baseId))
                .filter(x -> equipmentId == null || x.equipment.id.equals(equipmentId))
                .mapToInt(x -> x.quantity)
                .sum();

        int exp = expenditures.findAll().stream()
                .filter(x -> baseId == null || x.base.id.equals(baseId))
                .filter(x -> equipmentId == null || x.equipment.id.equals(equipmentId))
                .mapToInt(x -> x.quantity)
                .sum();

        return Map.of(
                "openingBalance", opening,
                "purchases", pur,
                "transferIn", tin,
                "transferOut", tout,
                "netMovement", pur + tin - tout,
                "assigned", assigned,
                "expended", exp,
                "closingBalance", closing
        );
    }
}