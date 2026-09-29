package com.mams.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mams.repository.AssignmentRepository;
import com.mams.repository.AuditLogRepository;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentRepository;
import com.mams.repository.ExpenditureRepository;
import com.mams.repository.InventoryRepository;
import com.mams.repository.PurchaseRepository;
import com.mams.repository.TransferRepository;
import com.mams.service.AssetService;

@RestController
@RequestMapping("/api")
public class AssetController {

    final AssetService s;
    final BaseRepository bases;
    final EquipmentRepository eq;
    final PurchaseRepository p;
    final TransferRepository t;
    final AssignmentRepository a;
    final ExpenditureRepository x;
    final AuditLogRepository logs;
    final InventoryRepository inventory;

    public AssetController(
            AssetService s,
            BaseRepository b,
            EquipmentRepository e,
            PurchaseRepository p,
            TransferRepository t,
            AssignmentRepository a,
            ExpenditureRepository x,
            AuditLogRepository l,
            InventoryRepository ir) {

        this.s = s;
        bases = b;
        eq = e;
        this.p = p;
        this.t = t;
        this.a = a;
        this.x = x;
        logs = l;
        inventory = ir;
    }

    @GetMapping("/bases")
    public Object bases() {
        return bases.findAll();
    }

    @GetMapping("/equipment")
    public Object equipment() {
        return eq.findAll();
    }

    @GetMapping("/dashboard/summary")
    public Object dash(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentId) {

        return s.dashboard(baseId, equipmentId);
    }

    @GetMapping("/inventory")
    public Object inventory(@RequestParam(required = false) Long baseId) {
        return baseId == null ? inventory.findAll() : inventory.findByBaseId(baseId);
    }

    @PostMapping("/purchases")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_OFFICER')")
    public Object purchase(
            @RequestBody Map<String, Object> b,
            Authentication auth) {

        return s.purchase(
                ((Number) b.get("baseId")).longValue(),
                ((Number) b.get("equipmentId")).longValue(),
                ((Number) b.get("quantity")).intValue(),
                String.valueOf(b.getOrDefault("reference", "")),
                auth.getName()
        );
    }

    @GetMapping("/purchases")
    public Object purchases(@RequestParam(required = false) Long baseId) {
        return baseId == null ? p.findAll() : p.findByBaseId(baseId);
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_OFFICER')")
    public Object transfer(
            @RequestBody Map<String, Object> b,
            Authentication auth) {

        return s.transfer(
                ((Number) b.get("fromBaseId")).longValue(),
                ((Number) b.get("toBaseId")).longValue(),
                ((Number) b.get("equipmentId")).longValue(),
                ((Number) b.get("quantity")).intValue(),
                String.valueOf(b.getOrDefault("reference", "")),
                auth.getName()
        );
    }

    @GetMapping("/transfers")
    public Object transfers() {
        return t.findAll();
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public Object assign(
            @RequestBody Map<String, Object> b,
            Authentication auth) {

        return s.assign(
                ((Number) b.get("baseId")).longValue(),
                ((Number) b.get("equipmentId")).longValue(),
                String.valueOf(b.get("personnelName")),
                ((Number) b.get("quantity")).intValue(),
                auth.getName()
        );
    }

    @GetMapping("/assignments")
    public Object assignments() {
        return a.findAll();
    }

    @PostMapping("/expenditures")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public Object expend(
            @RequestBody Map<String, Object> b,
            Authentication auth) {

        return s.expend(
                ((Number) b.get("baseId")).longValue(),
                ((Number) b.get("equipmentId")).longValue(),
                ((Number) b.get("quantity")).intValue(),
                String.valueOf(b.getOrDefault("reason", "")),
                auth.getName()
        );
    }

    @GetMapping("/expenditures")
    public Object expenditures() {
        return x.findAll();
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public Object audit() {
        return logs.findAll();
    }
}