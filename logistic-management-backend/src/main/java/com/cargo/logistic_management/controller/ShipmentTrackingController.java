package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.ShipmentTrackingResponseDto;
import com.cargo.logistic_management.service.ShipmentTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class ShipmentTrackingController {

    private final ShipmentTrackingService trackingService;

    @GetMapping("/{trackingCode}")
    public ResponseEntity<List<ShipmentTrackingResponseDto>> getHistory(@PathVariable String trackingCode) {
        List<ShipmentTrackingResponseDto> history = trackingService.getTrackingHistory(trackingCode);
        return ResponseEntity.ok(history);
    }
}