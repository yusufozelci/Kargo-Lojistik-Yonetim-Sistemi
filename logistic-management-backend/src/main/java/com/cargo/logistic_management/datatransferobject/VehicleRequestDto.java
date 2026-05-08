package com.cargo.logistic_management.datatransferobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequestDto {
    @NotBlank(message = "Plaka boş olamaz")
    private String plateNumber;

    @NotBlank(message = "Araç tipi boş olamaz")
    private String vehicleType;

    @NotNull(message = "Kapasite boş olamaz")
    private Double capacity;
}