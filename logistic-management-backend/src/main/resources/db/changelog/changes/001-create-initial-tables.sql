-- liquibase formatted sql

-- changeset furkan:1

-- 1. Roles
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       role_id BIGINT REFERENCES roles(id),
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100),
                       status BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. User_Tokens
CREATE TABLE user_tokens (
                             id SERIAL PRIMARY KEY,
                             user_id INT REFERENCES users(id) ON DELETE CASCADE,
                             token VARCHAR(255) NOT NULL,
                             token_type VARCHAR(20),
                             expires_at TIMESTAMP NOT NULL
);

-- 4. Customers
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           full_name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20),
                           customer_type VARCHAR(20)
);

-- 5. Addresses
CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           customer_id INT REFERENCES customers(id),
                           title VARCHAR(50),
                           city VARCHAR(50),
                           district VARCHAR(50),
                           full_address TEXT
);

-- 6. Branches
CREATE TABLE branches (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          city VARCHAR(50),
                          is_transfer_center BOOLEAN DEFAULT FALSE
);

-- 7. System_Logs
CREATE TABLE system_logs (
                             id SERIAL PRIMARY KEY,
                             user_id INT REFERENCES users(id),
                             action VARCHAR(100),
                             table_name VARCHAR(50),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Shipments
CREATE TABLE shipments (
                           id BIGSERIAL PRIMARY KEY,
                           tracking_code VARCHAR(20) UNIQUE NOT NULL,
                           sender_id INT REFERENCES customers(id),
                           receiver_id INT REFERENCES customers(id),
                           origin_address_id BIGINT REFERENCES addresses(id),
                           destination_address_id BIGINT REFERENCES addresses(id),
                           courier_id INT REFERENCES users(id),
                           status VARCHAR(50),
                           weight DOUBLE PRECISION,
                           distance DOUBLE PRECISION,
                           total_price DOUBLE PRECISION
);

-- 9. Shipment_Tracking
CREATE TABLE shipment_tracking (
                                   id BIGSERIAL PRIMARY KEY,
                                   shipment_id BIGINT REFERENCES shipments(id),
                                   status VARCHAR(50) NOT NULL,
                                   location_id BIGINT REFERENCES branches(id),
                                   description TEXT,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Vehicles
CREATE TABLE vehicles (
                          id BIGSERIAL PRIMARY KEY,
                          plate_number VARCHAR(20) UNIQUE,
                          vehicle_type VARCHAR(50),
                          capacity DOUBLE PRECISION
);

-- 11. Routes
CREATE TABLE routes (
                        id SERIAL PRIMARY KEY,
                        vehicle_id BIGINT REFERENCES vehicles(id),
                        departure_branch_id BIGINT REFERENCES branches(id),
                        arrival_branch_id BIGINT REFERENCES branches(id),
                        departure_time TIMESTAMP,
                        arrival_time TIMESTAMP
);

-- 12. Route_Shipments
CREATE TABLE route_shipments (
                                 id SERIAL PRIMARY KEY,
                                 route_id INT REFERENCES routes(id),
                                 shipment_id BIGINT REFERENCES shipments(id)
);

-- DUMMY DATA: Yusuf'un algoritma testleri için 10 adet kargo senaryosu
INSERT INTO roles (role_name) VALUES ('ADMIN'), ('COURIER'), ('CUSTOMER');

INSERT INTO users (role_id, email, password_hash, full_name)
VALUES (2, 'kurye1@kargo.com', 'hash123', 'Ahmet Kurye');

INSERT INTO customers (full_name, phone, customer_type)
VALUES
    ('Ali Veli', '5551112233', 'Individual'),
    ('Ayşe Fatma', '5559998877', 'Individual');

INSERT INTO addresses (customer_id, title, city, district, full_address)
VALUES
    (1, 'Ev', 'Kayseri', 'Melikgazi', 'Adres 1'),
    (2, 'İş', 'İzmir', 'Bornova', 'Adres 2');

INSERT INTO shipments (
    tracking_code, sender_id, receiver_id, origin_address_id, destination_address_id,
    courier_id, status, weight, distance, total_price
) VALUES
      ('TRK1000000001', 1, 2, 1, 2, 1, 'PENDING', 2.5, 850.0, NULL),
      ('TRK1000000002', 2, 1, 2, 1, 1, 'IN_TRANSIT', 1.0, 850.0, 150.0),
      ('TRK1000000003', 1, 2, 1, 2, 1, 'DELIVERED', 5.5, 120.0, 85.0),
      ('TRK1000000004', 2, 1, 2, 1, 1, 'CANCELLED', 10.0, 45.0, 0.0),
      ('TRK1000000005', 1, 2, 1, 2, 1, 'PENDING', 0.5, 900.0, NULL),
      ('TRK1000000006', 2, 1, 2, 1, 1, 'IN_TRANSIT', 15.2, 300.0, 450.0),
      ('TRK1000000007', 1, 2, 1, 2, 1, 'DELIVERED', 3.0, 15.0, 40.0),
      ('TRK1000000008', 2, 1, 2, 1, 1, 'PENDING', 8.4, 600.0, NULL),
      ('TRK1000000009', 1, 2, 1, 2, 1, 'IN_TRANSIT', 22.0, 1050.0, 1200.0),
      ('TRK1000000010', 2, 1, 2, 1, 1, 'DELIVERED', 1.2, 5.0, 30.0);