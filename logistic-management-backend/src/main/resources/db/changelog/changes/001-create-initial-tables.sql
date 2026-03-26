-- liquibase formatted sql

-- changeset furkan:1
-- 1. Roles (Kullanıcı Rolleri)
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Users (Sistem Kullanıcıları)
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       role_id INT REFERENCES roles(id),
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL, -- Şifreler hashlenerek saklanmalıdır [cite: 60, 74]
                       full_name VARCHAR(100),
                       status BOOLEAN DEFAULT TRUE, -- Aktif/Pasif kontrolü [cite: 67]
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. User_Tokens (Oturum ve Şifre Sıfırlama)
CREATE TABLE user_tokens (
                             id SERIAL PRIMARY KEY,
                             user_id INT REFERENCES users(id) ON DELETE CASCADE,
                             token VARCHAR(255) NOT NULL,
                             token_type VARCHAR(20), -- 'RESET', 'REMEMBER' [cite: 56]
                             expires_at TIMESTAMP NOT NULL
);

-- 4. Customers (Müşteri Bilgileri)
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           full_name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20),
                           customer_type VARCHAR(20) -- 'Individual', 'Corporate'
);

-- 5. Addresses (Müşteri Adresleri)
CREATE TABLE addresses (
                           id SERIAL PRIMARY KEY,
                           customer_id INT REFERENCES customers(id),
                           title VARCHAR(50), -- 'Ev', 'İş'
                           city VARCHAR(50),
                           district VARCHAR(50),
                           full_address TEXT
);

-- 6. Branches (Şube ve Transfer Merkezleri)
CREATE TABLE branches (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          city VARCHAR(50),
                          is_transfer_center BOOLEAN DEFAULT FALSE
);

-- 7. System_Logs (İşlem Logları) [cite: 68]
CREATE TABLE system_logs (
                             id SERIAL PRIMARY KEY,
                             user_id INT REFERENCES users(id),
                             action VARCHAR(100), -- 'CREATE', 'UPDATE', 'DELETE'
                             table_name VARCHAR(50),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Shipment_Statuses (Kargo Durum Tanımları)
CREATE TABLE shipment_statuses (
                                   id SERIAL PRIMARY KEY,
                                   status_name VARCHAR(50) UNIQUE -- 'Yolda', 'Teslim Edildi'
);

-- 9. Shipments (Ana Kargo Bilgileri) [cite: 107]
CREATE TABLE shipments (
                           id SERIAL PRIMARY KEY,
                           tracking_code VARCHAR(20) UNIQUE NOT NULL,
                           sender_id INT REFERENCES customers(id),
                           receiver_id INT REFERENCES customers(id),
                           origin_branch_id INT REFERENCES branches(id),
                           dest_branch_id INT REFERENCES branches(id),
                           status_id INT REFERENCES shipment_statuses(id),
                           weight DOUBLE PRECISION,
                           price DECIMAL(10,2)
);

-- 10. Shipment_Tracking (Kargo Hareket Geçmişi)
CREATE TABLE shipment_tracking (
                                   id SERIAL PRIMARY KEY,
                                   shipment_id INT REFERENCES shipments(id),
                                   status_id INT REFERENCES shipment_statuses(id),
                                   location_id INT REFERENCES branches(id),
                                   description TEXT,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Vehicles (Lojistik Araçları)
CREATE TABLE vehicles (
                          id SERIAL PRIMARY KEY,
                          plate_number VARCHAR(20) UNIQUE,
                          vehicle_type VARCHAR(50),
                          capacity DOUBLE PRECISION
);

-- 12. Routes (Şubeler Arası Seferler)
CREATE TABLE routes (
                        id SERIAL PRIMARY KEY,
                        vehicle_id INT REFERENCES vehicles(id),
                        departure_branch_id INT REFERENCES branches(id),
                        arrival_branch_id INT REFERENCES branches(id),
                        departure_time TIMESTAMP,
                        arrival_time TIMESTAMP
);

-- 13. Route_Shipments (Sefer ve Kargo Eşleştirmeleri)
CREATE TABLE route_shipments (
                                 id SERIAL PRIMARY KEY,
                                 route_id INT REFERENCES routes(id),
                                 shipment_id INT REFERENCES shipments(id)
);