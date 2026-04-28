package com.cargo.logistic_management.datatransferobject;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RouteRequestDto {

    @NotNull(message = "Araç ID boş olamaz")
    private Long vehicleId;

    @NotNull(message = "Çıkış şubesi boş olamaz")
    private Long departureBranchId;

    @NotNull(message = "Varış şubesi boş olamaz")
    private Long arrivalBranchId;

    @NotNull(message = "Kalkış zamanı boş olamaz")
    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;
}