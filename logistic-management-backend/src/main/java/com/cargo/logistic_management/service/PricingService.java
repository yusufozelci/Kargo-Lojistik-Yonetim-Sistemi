package com.cargo.logistic_management.service;

import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public double calculatePrice(double weight, double distanceKm) {
        double basePrice = 80.0;
        double weightPrice = calculateWeightPrice(weight);
        double distancePrice = calculateDistancePrice(distanceKm);

        double totalPrice = basePrice + weightPrice + distancePrice;

        return roundTwoDigits(totalPrice);
    }

    private double calculateWeightPrice(double weight) {
        if (weight <= 1) {
            return 25.0;
        }

        if (weight <= 5) {
            return 25.0 + ((weight - 1) * 18.0);
        }

        if (weight <= 10) {
            return 97.0 + ((weight - 5) * 15.0);
        }

        return 172.0 + ((weight - 10) * 12.0);
    }

    private double calculateDistancePrice(double distanceKm) {
        if (distanceKm <= 50) {
            return 35.0;
        }

        if (distanceKm <= 250) {
            return 35.0 + ((distanceKm - 50) * 0.55);
        }

        if (distanceKm <= 750) {
            return 145.0 + ((distanceKm - 250) * 0.35);
        }

        return 320.0 + ((distanceKm - 750) * 0.25);
    }

    private double roundTwoDigits(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}