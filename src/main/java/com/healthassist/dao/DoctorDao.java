package com.healthassist.dao;

import com.healthassist.db.Database;
import com.healthassist.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDao {
    public List<Doctor> findAll() throws SQLException {
        String sql = "SELECT * FROM doctors ORDER BY full_name";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<Doctor> doctors = new ArrayList<>();
            while (result.next()) {
                doctors.add(map(result));
            }
            return doctors;
        }
    }

    public Optional<Doctor> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE email = ?";
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

    public Doctor save(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (full_name, email, specialization, schedule) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, doctor);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    doctor.setId(keys.getInt(1));
                }
            }
            return doctor;
        }
    }

    public void update(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET full_name = ?, email = ?, specialization = ?, schedule = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, doctor);
            statement.setInt(5, doctor.getId());
            statement.executeUpdate();
        }
    }

    private void bind(PreparedStatement statement, Doctor doctor) throws SQLException {
        statement.setString(1, doctor.getFullName());
        statement.setString(2, doctor.getEmail());
        statement.setString(3, doctor.getSpecialization());
        statement.setString(4, doctor.getSchedule());
    }

    private Doctor map(ResultSet result) throws SQLException {
        return new Doctor(
                result.getInt("id"),
                result.getString("full_name"),
                result.getString("email"),
                result.getString("specialization"),
                result.getString("schedule")
        );
    }
}
