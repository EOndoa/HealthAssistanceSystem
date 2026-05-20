package com.healthassist.dao;

import com.healthassist.db.Database;
import com.healthassist.model.Patient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDao {
    public List<Patient> findAll() throws SQLException {
        String sql = "SELECT * FROM patients ORDER BY full_name";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<Patient> patients = new ArrayList<>();
            while (result.next()) {
                patients.add(map(result));
            }
            return patients;
        }
    }

    public Optional<Patient> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM patients WHERE email = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(map(result));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Patient> findByEmailOrName(String value) throws SQLException {
        String sql = "SELECT * FROM patients WHERE email = ? OR full_name = ? LIMIT 1";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, value);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(map(result));
                }
            }
        }
        return Optional.empty();
    }

    public List<Patient> findByDoctorId(int doctorId) throws SQLException {
        String sql = """
                SELECT DISTINCT p.*
                FROM patients p
                JOIN appointments a ON a.patient_id = p.id
                WHERE a.doctor_id = ? AND a.status = 'BOOKED'
                ORDER BY p.full_name
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, doctorId);
            try (ResultSet result = statement.executeQuery()) {
                List<Patient> patients = new ArrayList<>();
                while (result.next()) {
                    patients.add(map(result));
                }
                return patients;
            }
        }
    }

    public Patient save(Patient patient) throws SQLException {
        String sql = """
                INSERT INTO patients (full_name, email, phone, date_of_birth, address, health_record)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, patient);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    patient.setId(keys.getInt(1));
                }
            }
            return patient;
        }
    }

    public void update(Patient patient) throws SQLException {
        String sql = """
                UPDATE patients
                SET full_name = ?, email = ?, phone = ?, date_of_birth = ?, address = ?, health_record = ?
                WHERE id = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, patient);
            statement.setInt(7, patient.getId());
            statement.executeUpdate();
        }
    }

    private void bind(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getFullName());
        statement.setString(2, patient.getEmail());
        statement.setString(3, patient.getPhone());
        statement.setDate(4, patient.getDateOfBirth() == null ? null : Date.valueOf(patient.getDateOfBirth()));
        statement.setString(5, patient.getAddress());
        statement.setString(6, patient.getHealthRecord());
    }

    private Patient map(ResultSet result) throws SQLException {
        Date date = result.getDate("date_of_birth");
        return new Patient(
                result.getInt("id"),
                result.getString("full_name"),
                result.getString("email"),
                result.getString("phone"),
                date == null ? null : date.toLocalDate(),
                result.getString("address"),
                result.getString("health_record")
        );
    }
}
