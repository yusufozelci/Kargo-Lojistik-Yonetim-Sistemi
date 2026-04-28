package com.cargo.logistic_management.datatransferobject;

import com.cargo.logistic_management.entity.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ShipmentTrackingResponseDto {
    private ShipmentStatus status;
    private String description;
    private LocalDateTime dateTime;
}