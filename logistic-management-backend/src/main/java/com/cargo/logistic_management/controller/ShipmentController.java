package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.ShipmentRequestDto;
import com.cargo.logistic_management.datatransferobject.ShipmentResponseDto;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> listele() {
        return ResponseEntity.ok(shipmentService.tumKargolariGetir());
    }

    @PostMapping("/ekle")
    public ResponseEntity<ShipmentResponseDto> kargoEkle(@Valid @RequestBody ShipmentRequestDto requestDto) {
        ShipmentResponseDto createdShipment = shipmentService.kargoKaydet(requestDto);
        return new ResponseEntity<>(createdShipment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/durum")
    public ResponseEntity<ShipmentResponseDto> durumGuncelle(
            @PathVariable Long id,
            @RequestParam ShipmentStatus yeniDurum) {
        return ResponseEntity.ok(shipmentService.durumGuncelle(id, yeniDurum));
    }

    @GetMapping("/sorgula/{trackingCode}")
    public ResponseEntity<ShipmentResponseDto> kargoSorgula(@PathVariable String trackingCode) {
        return ResponseEntity.ok(shipmentService.kargoSorgula(trackingCode));
    }
}