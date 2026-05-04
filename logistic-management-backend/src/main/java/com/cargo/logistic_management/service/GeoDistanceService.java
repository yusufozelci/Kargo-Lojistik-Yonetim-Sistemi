package com.cargo.logistic_management.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeoDistanceService {

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final Map<String, Coordinates> geocodeCache = new ConcurrentHashMap<>();

    public GeoDistanceService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.jsonMapper = JsonMapper.builder().build();
    }

    public double calculateDistanceKm(
            String senderCity,
            String senderDistrict,
            String senderStreet,
            String receiverCity,
            String receiverDistrict,
            String receiverStreet
    ) {
        Coordinates origin = geocodeAddress(senderCity, senderDistrict, senderStreet);
        Coordinates destination = geocodeAddress(receiverCity, receiverDistrict, receiverStreet);

        return calculateRoadDistanceKm(origin, destination);
    }

    private Coordinates geocodeAddress(String city, String district, String street) {
        String cacheKey = normalize(city + "|" + district + "|" + street);

        if (geocodeCache.containsKey(cacheKey)) {
            return geocodeCache.get(cacheKey);
        }

        try {
            String query = street + ", " + district + ", " + city + ", Türkiye";

            String url = "https://nominatim.openstreetmap.org/search"
                    + "?format=json"
                    + "&limit=1"
                    + "&countrycodes=tr"
                    + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "CargoLogisticManagementStudentProject/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonNode root = jsonMapper.readTree(response.body());

            if (!root.isArray() || root.isEmpty()) {
                throw new RuntimeException("Adres koordinata çevrilemedi: " + query);
            }

            JsonNode firstResult = root.get(0);

            double latitude = firstResult.get("lat").asDouble();
            double longitude = firstResult.get("lon").asDouble();

            Coordinates coordinates = new Coordinates(latitude, longitude);
            geocodeCache.put(cacheKey, coordinates);

            return coordinates;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Adres bulunamadı veya mesafe hesaplanamadı: "
                            + city + " / " + district + " / " + street
            );
        }
    }

    private double calculateRoadDistanceKm(Coordinates origin, Coordinates destination) {
        try {
            String coordinates = origin.longitude() + "," + origin.latitude()
                    + ";"
                    + destination.longitude() + "," + destination.latitude();

            String url = "https://router.project-osrm.org/route/v1/driving/"
                    + coordinates
                    + "?overview=false";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonNode root = jsonMapper.readTree(response.body());

            if (!root.has("routes") || root.get("routes").isEmpty()) {
                throw new RuntimeException("Rota bulunamadı.");
            }

            double distanceMeters = root.get("routes").get(0).get("distance").asDouble();
            double distanceKm = distanceMeters / 1000.0;

            return roundTwoDigits(distanceKm);

        } catch (Exception e) {
            throw new RuntimeException("Yol mesafesi hesaplanamadı.");
        }
    }

    private double roundTwoDigits(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String normalize(String text) {
        return text
                .toLowerCase()
                .trim()
                .replace("ı", "i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ö", "o")
                .replace("ç", "c");
    }
}