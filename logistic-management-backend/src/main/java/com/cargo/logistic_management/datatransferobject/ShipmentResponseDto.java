package com.cargo.logistic_management.datatransferobject;

import com.cargo.logistic_management.entity.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShipmentResponseDto {
    private Long id;
    private String trackingCode;
    private String senderName;
    private String receiverName;
    private ShipmentStatus status;
    private Double totalPrice;
}