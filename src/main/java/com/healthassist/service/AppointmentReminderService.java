package com.healthassist.service;

import com.healthassist.dao.AppointmentDao;
import com.healthassist.model.Appointment;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppointmentReminderService {
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Set<Integer> shownAppointmentIds = new HashSet<>();

    public void start() {
        executor.scheduleAtFixedRate(this::checkReminders, 10, 60, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    private void checkReminders() {
        try {
            List<Appointment> appointments = appointmentDao.findStartingWithinMinutes(30);
            for (Appointment appointment : appointments) {
                if (shownAppointmentIds.add(appointment.getId())) {
                    Platform.runLater(() -> showReminder(appointment));
                }
            }
        } catch (SQLException ignored) {
            // The UI handles direct database failures; reminders retry on the next scheduled run.
        }
    }

    private void showReminder(Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Reminder");
        alert.setHeaderText("Upcoming appointment");
        alert.setContentText("Patient: " + appointment.getPatientName()
                + "\nDoctor: Dr. " + appointment.getDoctorName()
                + "\nTime: " + appointment.getAppointmentTime().format(formatter));
        alert.show();
    }
}
