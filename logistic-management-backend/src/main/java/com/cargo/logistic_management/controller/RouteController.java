package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.RouteRequestDto;
import com.cargo.logistic_management.entity.Route;
import com.cargo.logistic_management.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Route> createRoute(@Valid @RequestBody RouteRequestDto requestDto) {
        return new ResponseEntity<>(routeService.createRoute(requestDto), HttpStatus.CREATED);
    }

    @PostMapping("/{routeId}/load-shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<String> loadShipmentToRoute(@PathVariable Long routeId, @PathVariable Long shipmentId) {
        String response = routeService.addShipmentToRoute(routeId, shipmentId);
        return ResponseEntity.ok(response);
    }
}