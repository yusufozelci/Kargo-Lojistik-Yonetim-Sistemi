package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.RouteRequestDto;
import com.cargo.logistic_management.entity.*;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import com.cargo.logistic_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteShipmentRepository routeShipmentRepository;
    private final VehicleRepository vehicleRepository;
    private final BranchRepository branchRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingService trackingService;

    public Route createRoute(RouteRequestDto dto) {
        Route route = new Route();

        route.setVehicle(vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Araç bulunamadı!")));
        route.setDepartureBranch(branchRepository.findById(dto.getDepartureBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Çıkış şubesi bulunamadı!")));
        route.setArrivalBranch(branchRepository.findById(dto.getArrivalBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Varış şubesi bulunamadı!")));

        route.setDepartureTime(dto.getDepartureTime());
        route.setArrivalTime(dto.getArrivalTime());

        return routeRepository.save(route);
    }

    @Transactional
    public String addShipmentToRoute(Long routeId, Long shipmentId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Sefer bulunamadı!"));
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Kargo bulunamadı!"));

        RouteShipment routeShipment = new RouteShipment();
        routeShipment.setRoute(route);
        routeShipment.setShipment(shipment);
        routeShipmentRepository.save(routeShipment);

        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipmentRepository.save(shipment);

        String logMesaji = "Kargo " + route.getVehicle().getPlateNumber() + " plakalı araç ile " +
                route.getDepartureBranch().getName() + " şubesinden yola çıktı.";
        trackingService.createLog(shipment, ShipmentStatus.IN_TRANSIT, logMesaji);

        return "Kargo başarıyla araca yüklendi ve yola çıkarıldı.";
    }

    public List<Shipment> getShipmentsByRoute(Long routeId) {
        return routeShipmentRepository.findByRouteId(routeId).stream()
                .map(RouteShipment::getShipment)
                .collect(Collectors.toList());
    }
}