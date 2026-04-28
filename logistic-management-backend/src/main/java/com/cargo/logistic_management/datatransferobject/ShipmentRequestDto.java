package com.cargo.logistic_management.datatransferobject;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ShipmentRequestDto {

    @NotNull(message = "Gönderici ID boş bırakılamaz!")
    private Long senderId;

    @NotNull(message = "Alıcı ID boş bırakılamaz!")
    private Long receiverId;

    @NotNull(message = "Çıkış adresi boş bırakılamaz!")
    private Long originAddressId;

    @NotNull(message = "Varış adresi boş bırakılamaz!")
    private Long destinationAddressId;

    private Long courierId;

    @NotNull(message = "Kargo ağırlığı girilmelidir!")
    @Positive(message = "Kargo ağırlığı sıfırdan büyük olmalıdır!")
    private Double weight;

    @NotNull(message = "Mesafe bilgisi girilmelidir!")
    @Positive(message = "Mesafe sıfırdan büyük olmalıdır!")
    private Double distance;
}