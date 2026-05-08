package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.AddressResponseDto;
import com.cargo.logistic_management.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@RequestBody com.cargo.logistic_management.datatransferobject.AddressRequestDto addressRequestDto) {
        return new org.springframework.http.ResponseEntity<>(addressService.createAddress(addressRequestDto), org.springframework.http.HttpStatus.CREATED);
    }
}