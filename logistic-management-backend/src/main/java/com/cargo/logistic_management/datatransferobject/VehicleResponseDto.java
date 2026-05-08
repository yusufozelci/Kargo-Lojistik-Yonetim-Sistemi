package com.cargo.logistic_management.datatransferobject;

import lombok.Data;

@Data
public class VehicleResponseDto {
    private Long id;
    private String plateNumber;
    private String vehicleType;
    private Double capacity;
}