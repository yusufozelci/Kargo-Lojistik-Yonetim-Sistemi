package com.cargo.logistic_management.datatransferobject;

import com.cargo.logistic_management.entity.ShipmentStatus;
import lombok.Data;

@Data
public class ShipmentRequestDto {
    private Long senderId;
    private Long receiverId;
    private Long originAddressId;
    private Long destinationAddressId;
    private Long courierId;
    private Double weight;
    private Double distance;
}