CREATE DATABASE IF NOT EXISTS health_assistance;
USE health_assistance;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    role ENUM('PATIENT', 'DOCTOR', 'ADMINISTRATOR') NOT NULL
);

CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(30),
    date_of_birth DATE,
    address VARCHAR(255),
    health_record TEXT
);

CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    specialization VARCHAR(120) NOT NULL,
    schedule VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_time DATETIME NOT NULL,
    reason VARCHAR(255),
    status ENUM('BOOKED', 'CANCELLED') NOT NULL DEFAULT 'BOOKED',
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT uq_doctor_slot UNIQUE (doctor_id, appointment_time)
);

INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMINISTRATOR')
ON DUPLICATE KEY UPDATE username = username;
