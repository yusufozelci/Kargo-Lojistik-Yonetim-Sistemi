package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.VehicleRequestDto;
import com.cargo.logistic_management.datatransferobject.VehicleResponseDto;
import com.cargo.logistic_management.entity.Vehicle;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import com.cargo.logistic_management.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<VehicleResponseDto> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public VehicleResponseDto createVehicle(VehicleRequestDto dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setCapacity(dto.getCapacity());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return convertToResponseDto(savedVehicle);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Araç bulunamadı!");
        }
        vehicleRepository.deleteById(id);
    }

    private VehicleResponseDto convertToResponseDto(Vehicle vehicle) {
        VehicleResponseDto responseDto = new VehicleResponseDto();
        responseDto.setId(vehicle.getId());
        responseDto.setPlateNumber(vehicle.getPlateNumber());
        responseDto.setVehicleType(vehicle.getVehicleType());
        responseDto.setCapacity(vehicle.getCapacity());
        return responseDto;
    }
}