package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.BranchRequestDto;
import com.cargo.logistic_management.datatransferobject.BranchResponseDto;
import com.cargo.logistic_management.entity.Branch;
import com.cargo.logistic_management.repository.BranchRepository;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<BranchResponseDto> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BranchResponseDto createBranch(BranchRequestDto requestDto) {
        if (branchRepository.findByName(requestDto.getName()) != null) {
            throw new RuntimeException("Bu isimde bir şube zaten mevcut!");
        }

        Branch branch = new Branch();
        branch.setName(requestDto.getName());
        branch.setCity(requestDto.getCity());
        branch.setIsTransferCenter(requestDto.getIsTransferCenter());

        Branch savedBranch = branchRepository.save(branch);
        return convertToDto(savedBranch);
    }

    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " ID'li şube bulunamadı!"));
        branchRepository.delete(branch);
    }

    private BranchResponseDto convertToDto(Branch branch) {
        return new BranchResponseDto(
                branch.getId(),
                branch.getName(),
                branch.getCity(),
                branch.getIsTransferCenter()
        );
    }
}