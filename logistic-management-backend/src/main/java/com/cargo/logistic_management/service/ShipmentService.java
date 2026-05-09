package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.ShipmentRequestDto;
import com.cargo.logistic_management.datatransferobject.ShipmentResponseDto;
import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
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
    private final ShipmentTrackingService trackingService;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final PricingService pricingService;

    public List<ShipmentResponseDto> tumKargolariGetir() {
        return shipmentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ShipmentResponseDto> kullaniciKargolariniGetir(String telefon) {
        return shipmentRepository.findAllBySenderPhoneOrReceiverPhone(telefon, telefon)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipmentResponseDto kargoKaydet(ShipmentRequestDto dto) {
        Shipment shipment = new Shipment();

        shipment.setSender(customerRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Gönderici bulunamadı! ID: " + dto.getSenderId())));

        shipment.setReceiver(customerRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Alıcı bulunamadı! ID: " + dto.getReceiverId())));

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

        if (dto.getWeight() != null && dto.getDistance() != null) {
            shipment.setTotalPrice(pricingService.calculatePrice(dto.getWeight(), dto.getDistance()));
        }

        Shipment kaydedilenKargo = shipmentRepository.save(shipment);

        trackingService.createLog(kaydedilenKargo, ShipmentStatus.PENDING, "Kargo sisteme başarıyla kaydedildi.");

        return convertToResponseDto(kaydedilenKargo);
    }

    @Transactional
    public ShipmentResponseDto durumGuncelle(Long id, ShipmentStatus yeniDurum) {
        Shipment kargo = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kargo bulunamadı! ID: " + id));

        if (kargo.getStatus() == ShipmentStatus.DELIVERED) {
            throw new RuntimeException("Teslim edilmiş kargo üzerinde işlem yapılamaz!");
        }

        kargo.setStatus(yeniDurum);
        Shipment guncellenenKargo = shipmentRepository.save(kargo);
        trackingService.createLog(guncellenenKargo, yeniDurum, "Kargo durumu '" + yeniDurum + "' olarak güncellendi.");

        return convertToResponseDto(guncellenenKargo);
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
                shipment.getSender() != null ? shipment.getSender().getFullName() : "Bilinmeyen Gönderici",
                shipment.getReceiver() != null ? shipment.getReceiver().getFullName() : "Bilinmeyen Alıcı",
                shipment.getStatus(),
                shipment.getTotalPrice()
        );
    }

    @Transactional
    public ShipmentResponseDto kuryeAta(Long shipmentId, Long courierId) {
        Shipment kargo = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Kargo bulunamadı!"));

        var kurye = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Kurye bulunamadı!"));
        long atanmisKargoSayisi = shipmentRepository.countByCourierId(courierId);

        if (atanmisKargoSayisi >= 10) {
            throw new RuntimeException("Kurye kapasitesi dolu! (Maksimum 10 kargo)");
        }

        kargo.setCourier(kurye);

        kargo.setStatus(com.cargo.logistic_management.entity.ShipmentStatus.IN_TRANSIT);

        Shipment guncellenenKargo = shipmentRepository.save(kargo);

        trackingService.createLog(guncellenenKargo, guncellenenKargo.getStatus(),
                "Kargo, kurye " + kurye.getFullName() + " üzerine zimmetlendi ve dağıtım süreci başladı.");

        return convertToResponseDto(guncellenenKargo);
    }
}