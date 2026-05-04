package com.cargo.logistic_management.datatransferobject;

import com.cargo.logistic_management.entity.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicShipmentResponseDto {
    private Long shipmentId;
    private String trackingCode;
    private String senderName;
    private String receiverName;
    private ShipmentStatus status;
    private Double weight;
    private Double distance;
    private Double totalPrice;
}