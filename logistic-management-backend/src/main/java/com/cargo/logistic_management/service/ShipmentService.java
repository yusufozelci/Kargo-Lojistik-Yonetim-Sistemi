package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.ShipmentRequestDto;
import com.cargo.logistic_management.datatransferobject.ShipmentResponseDto;
import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.entity.ShipmentTracking;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import com.cargo.logistic_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingRepository shipmentTrackingRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<ShipmentResponseDto> tumKargolariGetir() {
        return shipmentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipmentResponseDto kargoKaydet(ShipmentRequestDto dto) {
        Shipment shipment = new Shipment();

        shipment.setSender(customerRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Gönderici müşteri bulunamadı! ID: " + dto.getSenderId())));

        shipment.setReceiver(customerRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Alıcı müşteri bulunamadı! ID: " + dto.getReceiverId())));

        shipment.setOriginAddress(addressRepository.findById(dto.getOriginAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Çıkış adresi bulunamadı!")));

        shipment.setDestinationAddress(addressRepository.findById(dto.getDestinationAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Varış adresi bulunamadı!")));

        if (dto.getCourierId() != null) {
            shipment.setCourier(userRepository.findById(dto.getCourierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kurye bulunamadı!")));
        }

        shipment.setWeight(dto.getWeight());
        shipment.setDistance(dto.getDistance());
        shipment.setTrackingCode("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        shipment.setStatus(ShipmentStatus.PENDING);

        double birimKatsayi = 15.0;
        if (dto.getWeight() != null && dto.getDistance() != null) {
            shipment.setTotalPrice(dto.getWeight() * dto.getDistance() * birimKatsayi);
        }

        Shipment kaydedilenKargo = shipmentRepository.save(shipment);

        createTrackingLog(kaydedilenKargo, ShipmentStatus.PENDING, "Kargo sisteme başarıyla kaydedildi.");

        return convertToResponseDto(kaydedilenKargo);
    }

    @Transactional
    public ShipmentResponseDto durumGuncelle(Long id, ShipmentStatus yeniDurum) {
        Shipment kargo = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kargo bulunamadı! ID: " + id));

        if (kargo.getStatus() == ShipmentStatus.DELIVERED) {
            throw new RuntimeException("Teslim edilmiş kargo üzerinde durum değişikliği yapılamaz!");
        }

        kargo.setStatus(yeniDurum);
        Shipment guncellenenKargo = shipmentRepository.save(kargo);

        createTrackingLog(guncellenenKargo, yeniDurum, "Kargo durumu '" + yeniDurum + "' olarak güncellendi.");

        return convertToResponseDto(guncellenenKargo);
    }


    private void createTrackingLog(Shipment shipment, ShipmentStatus status, String description) {
        ShipmentTracking trackingLog = new ShipmentTracking();
        trackingLog.setShipment(shipment);
        trackingLog.setStatus(status);
        trackingLog.setDescription(description);
        shipmentTrackingRepository.save(trackingLog);
    }

    public ShipmentResponseDto kargoSorgula(String trackingCode) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Bu takip koduyla bir kargo bulunamadı: " + trackingCode));
        return convertToResponseDto(shipment);
    }

    private ShipmentResponseDto convertToResponseDto(Shipment shipment) {
        return new ShipmentResponseDto(
                shipment.getId(),
                shipment.getTrackingCode(),
                shipment.getSender().getFullName(), // getFirstName yerine getFullName kullanıldı
                shipment.getReceiver().getFullName(), // getFirstName yerine getFullName kullanıldı
                shipment.getStatus(),
                shipment.getTotalPrice()
        );
    }
}