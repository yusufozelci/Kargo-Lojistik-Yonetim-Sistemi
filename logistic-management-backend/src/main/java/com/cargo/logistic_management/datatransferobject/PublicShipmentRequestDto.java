package com.cargo.logistic_management.datatransferobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PublicShipmentRequestDto {

    @NotBlank(message = "Gönderici adı soyadı boş bırakılamaz.")
    private String senderFullName;

    @NotBlank(message = "Gönderici telefonu boş bırakılamaz.")
    private String senderPhone;

    @NotBlank(message = "Gönderici ili boş bırakılamaz.")
    private String senderCity;

    @NotBlank(message = "Gönderici ilçesi boş bırakılamaz.")
    private String senderDistrict;

    @NotBlank(message = "Gönderici sokak bilgisi boş bırakılamaz.")
    private String senderStreet;


    @NotBlank(message = "Alıcı adı soyadı boş bırakılamaz.")
    private String receiverFullName;

    @NotBlank(message = "Alıcı telefonu boş bırakılamaz.")
    private String receiverPhone;

    @NotBlank(message = "Alıcı ili boş bırakılamaz.")
    private String receiverCity;

    @NotBlank(message = "Alıcı ilçesi boş bırakılamaz.")
    private String receiverDistrict;

    @NotBlank(message = "Alıcı sokak bilgisi boş bırakılamaz.")
    private String receiverStreet;


    @NotNull(message = "Kargo ağırlığı boş bırakılamaz.")
    @Positive(message = "Kargo ağırlığı sıfırdan büyük olmalıdır.")
    private Double weight;
}