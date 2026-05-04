package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ShipmentRepository shipmentRepository;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("UserRegisterDto", new UserRegisterDto());
        return "register";
    }

    @GetMapping("/tracking")
    public String trackingPage(
            @RequestParam(value = "trackingNumber", required = false) String trackingNumber,
            Model model
    ) {
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            Optional<Shipment> shipmentOpt = shipmentRepository.findByTrackingCode(trackingNumber);

            if (shipmentOpt.isPresent()) {
                model.addAttribute("shipment", shipmentOpt.get());
            } else {
                model.addAttribute("error", "Bu takip numarasına ait bir kargo bulunamadı.");
            }
        }

        return "tracking";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}