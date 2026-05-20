package com.healthassist.model;

import java.time.LocalDateTime;

public class Appointment {
    private final int id;
    private final int patientId;
    private final String patientName;
    private final int doctorId;
    private final String doctorName;
    private final LocalDateTime appointmentTime;
    private final String reason;
    private final String status;

    public Appointment(int id, int patientId, String patientName, int doctorId, String doctorName,
                       LocalDateTime appointmentTime, String reason, String status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }
}
