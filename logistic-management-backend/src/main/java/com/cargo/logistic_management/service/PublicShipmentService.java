package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.PublicShipmentRequestDto;
import com.cargo.logistic_management.datatransferobject.PublicShipmentResponseDto;
import com.cargo.logistic_management.entity.Address;
import com.cargo.logistic_management.entity.Customer;
import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.repository.AddressRepository;
import com.cargo.logistic_management.repository.CustomerRepository;
import com.cargo.logistic_management.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicShipmentService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingService trackingService;
    private final GeoDistanceService geoDistanceService;

    private static final double PRICE_COEFFICIENT = 15.0;

    @Transactional
    public PublicShipmentResponseDto createPublicShipment(PublicShipmentRequestDto dto) {

        double distanceKm = geoDistanceService.calculateDistanceKm(
                dto.getSenderCity(),
                dto.getSenderDistrict(),
                dto.getSenderStreet(),
                dto.getReceiverCity(),
                dto.getReceiverDistrict(),
                dto.getReceiverStreet()
        );

        double totalPrice = calculatePrice(dto.getWeight(), distanceKm);

        Customer sender = new Customer();
        sender.setFullName(dto.getSenderFullName());
        sender.setPhone(dto.getSenderPhone());
        sender.setCustomerType("SENDER");
        Customer savedSender = customerRepository.save(sender);

        Customer receiver = new Customer();
        receiver.setFullName(dto.getReceiverFullName());
        receiver.setPhone(dto.getReceiverPhone());
        receiver.setCustomerType("RECEIVER");
        Customer savedReceiver = customerRepository.save(receiver);

        Address senderAddress = new Address();
        senderAddress.setCustomer(savedSender);
        senderAddress.setTitle("Gönderici Adresi");
        senderAddress.setCity(dto.getSenderCity());
        senderAddress.setDistrict(dto.getSenderDistrict());
        senderAddress.setFullAddress(dto.getSenderStreet());
        Address savedSenderAddress = addressRepository.save(senderAddress);

        Address receiverAddress = new Address();
        receiverAddress.setCustomer(savedReceiver);
        receiverAddress.setTitle("Alıcı Adresi");
        receiverAddress.setCity(dto.getReceiverCity());
        receiverAddress.setDistrict(dto.getReceiverDistrict());
        receiverAddress.setFullAddress(dto.getReceiverStreet());
        Address savedReceiverAddress = addressRepository.save(receiverAddress);

        Shipment shipment = new Shipment();
        shipment.setTrackingCode(generateTrackingCode());
        shipment.setSender(savedSender);
        shipment.setReceiver(savedReceiver);
        shipment.setOriginAddress(savedSenderAddress);
        shipment.setDestinationAddress(savedReceiverAddress);
        shipment.setWeight(dto.getWeight());
        shipment.setDistance(distanceKm);
        shipment.setTotalPrice(totalPrice);
        shipment.setStatus(ShipmentStatus.PENDING);

        Shipment savedShipment = shipmentRepository.save(shipment);

        trackingService.createLog(
                savedShipment,
                ShipmentStatus.PENDING,
                "Gönderi müşteri panelinden oluşturuldu. Kurye çağırma talebi alındı."
        );

        return new PublicShipmentResponseDto(
                savedShipment.getId(),
                savedShipment.getTrackingCode(),
                savedSender.getFullName(),
                savedReceiver.getFullName(),
                savedShipment.getStatus(),
                savedShipment.getWeight(),
                savedShipment.getDistance(),
                savedShipment.getTotalPrice()
        );
    }

    public PublicShipmentResponseDto calculateQuote(PublicShipmentRequestDto dto) {
        double distanceKm = geoDistanceService.calculateDistanceKm(
                dto.getSenderCity(),
                dto.getSenderDistrict(),
                dto.getSenderStreet(),
                dto.getReceiverCity(),
                dto.getReceiverDistrict(),
                dto.getReceiverStreet()
        );

        double totalPrice = calculatePrice(dto.getWeight(), distanceKm);

        return new PublicShipmentResponseDto(
                null,
                null,
                dto.getSenderFullName(),
                dto.getReceiverFullName(),
                ShipmentStatus.PENDING,
                dto.getWeight(),
                distanceKm,
                totalPrice
        );
    }

    private double calculatePrice(double weight, double distanceKm) {
        double price = weight * distanceKm * PRICE_COEFFICIENT;
        return Math.round(price * 100.0) / 100.0;
    }

    private String generateTrackingCode() {
        return "TRK-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}