package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.PublicShipmentRequestDto;
import com.cargo.logistic_management.datatransferobject.PublicShipmentResponseDto;
import com.cargo.logistic_management.service.PublicShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/shipments")
@RequiredArgsConstructor
public class PublicShipmentController {

    private final PublicShipmentService publicShipmentService;

    @PostMapping("/quote")
    public ResponseEntity<PublicShipmentResponseDto> calculateQuote(
            @Valid @RequestBody PublicShipmentRequestDto requestDto
    ) {
        return ResponseEntity.ok(publicShipmentService.calculateQuote(requestDto));
    }

    @PostMapping("/create")
    public ResponseEntity<PublicShipmentResponseDto> createShipment(
            @Valid @RequestBody PublicShipmentRequestDto requestDto
    ) {
        PublicShipmentResponseDto response = publicShipmentService.createPublicShipment(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}