package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.BranchRequestDto;
import com.cargo.logistic_management.datatransferobject.BranchResponseDto;
import com.cargo.logistic_management.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'CUSTOMER')")
    public ResponseEntity<List<BranchResponseDto>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BranchResponseDto> createBranch(@Valid @RequestBody BranchRequestDto requestDto) {
        return new ResponseEntity<>(branchService.createBranch(requestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}