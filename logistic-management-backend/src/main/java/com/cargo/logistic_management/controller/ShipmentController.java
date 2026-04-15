package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.service.ShipmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public List<Shipment> listele() {
        return shipmentService.tumKargolariGetir();
    }

    @PostMapping("/ekle")
    public Shipment kargoEkle(@RequestBody Shipment shipment) {
        return shipmentService.kargoKaydet(shipment);
    }
    @PutMapping("/{id}/durum")
    public Shipment durumGuncelle(@PathVariable Long id, @RequestParam ShipmentStatus yeniDurum) {
        return shipmentService.durumGuncelle(id, yeniDurum);
    }

}