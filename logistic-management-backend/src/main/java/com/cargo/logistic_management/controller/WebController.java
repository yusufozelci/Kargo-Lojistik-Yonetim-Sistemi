package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.repository.ShipmentRepository;
import com.cargo.logistic_management.repository.UserRepository;
import com.cargo.logistic_management.service.BranchService;
import com.cargo.logistic_management.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ShipmentRepository shipmentRepository;
    private final PricingService pricingService;
    private final BranchService branchService;
    private final com.cargo.logistic_management.repository.UserRepository userRepository;
    private final com.cargo.logistic_management.service.ShipmentService shipmentService;


    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("trackingNumber", "");
        return "index";
    }

    @GetMapping("/tracking")
    public String trackingPage(
            @RequestParam(value = "trackingNumber", required = false) String trackingNumber,
            Model model
    ) {
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            Optional<Shipment> shipmentOpt = shipmentRepository.findByTrackingCode(trackingNumber.trim());

            if (shipmentOpt.isPresent()) {
                model.addAttribute("shipment", shipmentOpt.get());
                model.addAttribute("trackingNumber", trackingNumber);
            } else {
                model.addAttribute("error", "Bu takip numarasına ait bir kargo bulunamadı.");
                model.addAttribute("trackingNumber", trackingNumber);
            }
        }

        return "index";
    }

    @GetMapping("/calculate-price")
    public String calculatePrice(
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "distance", required = false) Double distance,
            Model model
    ) {
        if (weight == null || distance == null || weight <= 0 || distance <= 0) {
            model.addAttribute("priceError", "Ağırlık ve mesafe sıfırdan büyük olmalıdır.");
            return "index";
        }

        double totalPrice = pricingService.calculatePrice(weight, distance);

        model.addAttribute("weight", weight);
        model.addAttribute("distance", distance);
        model.addAttribute("calculatedPrice", totalPrice);

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

    @GetMapping("/user-dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        addUserDashboardBaseData(authentication, model);

        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        model.addAttribute("myShipments", shipmentService.kullaniciKargolariniGetir(user.getPhone()));
        return "user-dashboard";
    }

    @GetMapping("/user-dashboard/tracking")
    public String userDashboardTracking(
            @RequestParam(value = "trackingNumber", required = false) String trackingNumber,
            Authentication authentication,
            Model model
    ) {
        addUserDashboardBaseData(authentication, model);

        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            Optional<Shipment> shipmentOpt = shipmentRepository.findByTrackingCode(trackingNumber.trim());

            if (shipmentOpt.isPresent()) {
                model.addAttribute("userShipment", shipmentOpt.get());
                model.addAttribute("userTrackingNumber", trackingNumber);
            } else {
                model.addAttribute("userTrackingError", "Bu takip numarasına ait bir kargo bulunamadı.");
                model.addAttribute("userTrackingNumber", trackingNumber);
            }
        }

        return "user-dashboard";
    }

    @GetMapping("/user-dashboard/calculate-price")
    public String userDashboardCalculatePrice(
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "distance", required = false) Double distance,
            Authentication authentication,
            Model model
    ) {
        addUserDashboardBaseData(authentication, model);

        if (weight == null || distance == null || weight <= 0 || distance <= 0) {
            model.addAttribute("userPriceError", "Ağırlık ve mesafe sıfırdan büyük olmalıdır.");
            return "user-dashboard";
        }

        double totalPrice = pricingService.calculatePrice(weight, distance);

        model.addAttribute("userWeight", weight);
        model.addAttribute("userDistance", distance);
        model.addAttribute("userCalculatedPrice", totalPrice);

        return "user-dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    private void addUserDashboardBaseData(Authentication authentication, Model model) {
        model.addAttribute("email", authentication.getName());
    }
}