package com.cargo.logistic_management.service;

import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.entity.ShipmentTracking;
import com.cargo.logistic_management.repository.ShipmentRepository;
import com.cargo.logistic_management.repository.ShipmentTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingRepository shipmentTrackingRepository;

    public List<Shipment> tumKargolariGetir() {
        return shipmentRepository.findAll();
    }

    @Transactional
    public Shipment kargoKaydet(Shipment shipment) {
        if (shipment.getTrackingCode() == null) {
            shipment.setTrackingCode("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        shipment.setStatus(ShipmentStatus.PENDING);

        double birimKatsayi = 15.0;
        if (shipment.getWeight() != null && shipment.getDistance() != null) {
            shipment.setTotalPrice(shipment.getWeight() * shipment.getDistance() * birimKatsayi);
        }

        Shipment kaydedilenKargo = shipmentRepository.save(shipment);

        ShipmentTracking ilkLog = new ShipmentTracking();
        ilkLog.setShipment(kaydedilenKargo);
        ilkLog.setStatus(ShipmentStatus.PENDING);
        ilkLog.setDescription("Kargo sisteme başarıyla kaydedildi.");
        shipmentTrackingRepository.save(ilkLog);

        return kaydedilenKargo;
    }

    @Transactional
    public Shipment durumGuncelle(Long id, ShipmentStatus yeniDurum) {
        Shipment kargo = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kargo bulunamadı!"));

        if (kargo.getStatus() == ShipmentStatus.DELIVERED) {
            throw new RuntimeException("Teslim edilmiş kargo güncellenemez!");
        }

        kargo.setStatus(yeniDurum);
        Shipment guncelKargo = shipmentRepository.save(kargo);

        ShipmentTracking trackingLog = new ShipmentTracking();
        trackingLog.setShipment(guncelKargo);
        trackingLog.setStatus(yeniDurum);
        trackingLog.setDescription("Kargo durumu '" + yeniDurum + "' olarak güncellendi.");
        shipmentTrackingRepository.save(trackingLog);

        return guncelKargo;
    }
}