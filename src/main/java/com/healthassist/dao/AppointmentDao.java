package com.healthassist.dao;

import com.healthassist.db.Database;
import com.healthassist.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {
    public List<Appointment> findUpcoming() throws SQLException {
        String sql = """
                SELECT a.id, a.patient_id, p.full_name AS patient_name, a.doctor_id, d.full_name AS doctor_name,
                       a.appointment_time, a.reason, a.status
                FROM appointments a
                JOIN patients p ON p.id = a.patient_id
                JOIN doctors d ON d.id = a.doctor_id
                WHERE a.status = 'BOOKED' AND a.appointment_time >= NOW()
                ORDER BY a.appointment_time
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<Appointment> appointments = new ArrayList<>();
            while (result.next()) {
                appointments.add(map(result));
            }
            return appointments;
        }
    }

    public List<Appointment> findUpcomingByDoctorId(int doctorId) throws SQLException {
        String sql = """
                SELECT a.id, a.patient_id, p.full_name AS patient_name, a.doctor_id, d.full_name AS doctor_name,
                       a.appointment_time, a.reason, a.status
                FROM appointments a
                JOIN patients p ON p.id = a.patient_id
                JOIN doctors d ON d.id = a.doctor_id
                WHERE a.status = 'BOOKED' AND a.appointment_time >= NOW() AND a.doctor_id = ?
                ORDER BY a.appointment_time
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, doctorId);
            try (ResultSet result = statement.executeQuery()) {
                List<Appointment> appointments = new ArrayList<>();
                while (result.next()) {
                    appointments.add(map(result));
                }
                return appointments;
            }
        }
    }

    public List<Appointment> findUpcomingByPatientId(int patientId) throws SQLException {
        String sql = """
                SELECT a.id, a.patient_id, p.full_name AS patient_name, a.doctor_id, d.full_name AS doctor_name,
                       a.appointment_time, a.reason, a.status
                FROM appointments a
                JOIN patients p ON p.id = a.patient_id
                JOIN doctors d ON d.id = a.doctor_id
                WHERE a.status = 'BOOKED' AND a.appointment_time >= NOW() AND a.patient_id = ?
                ORDER BY a.appointment_time
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patientId);
            try (ResultSet result = statement.executeQuery()) {
                List<Appointment> appointments = new ArrayList<>();
                while (result.next()) {
                    appointments.add(map(result));
                }
                return appointments;
            }
        }
    }

    public List<Appointment> findStartingWithinMinutes(int minutes) throws SQLException {
        String sql = """
                SELECT a.id, a.patient_id, p.full_name AS patient_name, a.doctor_id, d.full_name AS doctor_name,
                       a.appointment_time, a.reason, a.status
                FROM appointments a
                JOIN patients p ON p.id = a.patient_id
                JOIN doctors d ON d.id = a.doctor_id
                WHERE a.status = 'BOOKED'
                  AND a.appointment_time BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? MINUTE)
                ORDER BY a.appointment_time
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, minutes);
            try (ResultSet result = statement.executeQuery()) {
                List<Appointment> appointments = new ArrayList<>();
                while (result.next()) {
                    appointments.add(map(result));
                }
                return appointments;
            }
        }
    }

    public void book(int patientId, int doctorId, LocalDateTime appointmentTime, String reason) throws SQLException {
        String conflictSql = """
                SELECT COUNT(*) FROM appointments
                WHERE doctor_id = ? AND appointment_time = ? AND status = 'BOOKED'
                """;
        String insertSql = """
                INSERT INTO appointments (patient_id, doctor_id, appointment_time, reason, status)
                VALUES (?, ?, ?, ?, 'BOOKED')
                """;
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement conflict = connection.prepareStatement(conflictSql)) {
                conflict.setInt(1, doctorId);
                conflict.setTimestamp(2, Timestamp.valueOf(appointmentTime));
                try (ResultSet result = conflict.executeQuery()) {
                    result.next();
                    if (result.getInt(1) > 0) {
                        connection.rollback();
                        throw new SQLException("Scheduling conflict: the selected doctor already has an appointment at that time.");
                    }
                }
            }

            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, patientId);
                insert.setInt(2, doctorId);
                insert.setTimestamp(3, Timestamp.valueOf(appointmentTime));
                insert.setString(4, reason);
                insert.executeUpdate();
            }
            connection.commit();
        }
    }

    public void cancel(int appointmentId) throws SQLException {
        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, appointmentId);
            statement.executeUpdate();
        }
    }

    private Appointment map(ResultSet result) throws SQLException {
        return new Appointment(
                result.getInt("id"),
                result.getInt("patient_id"),
                result.getString("patient_name"),
                result.getInt("doctor_id"),
                result.getString("doctor_name"),
                result.getTimestamp("appointment_time").toLocalDateTime(),
                result.getString("reason"),
                result.getString("status")
        );
    }
}
