package com.healthassist.model;

public class Doctor {
    private int id;
    private String fullName;
    private String email;
    private String specialization;
    private String schedule;

    public Doctor(int id, String fullName, String email, String specialization, String schedule) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.specialization = specialization;
        this.schedule = schedule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return id + " - Dr. " + fullName;
    }
}
