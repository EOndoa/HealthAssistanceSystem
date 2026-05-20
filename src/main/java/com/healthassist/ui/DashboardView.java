package com.healthassist.ui;

import com.healthassist.dao.AppointmentDao;
import com.healthassist.dao.DoctorDao;
import com.healthassist.dao.PatientDao;
import com.healthassist.dao.UserDao;
import com.healthassist.model.Appointment;
import com.healthassist.model.Doctor;
import com.healthassist.model.Patient;
import com.healthassist.model.Role;
import com.healthassist.model.User;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DashboardView {
    private final Stage stage;
    private final User user;
    private final PatientDao patientDao = new PatientDao();
    private final DoctorDao doctorDao = new DoctorDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final UserDao userDao = new UserDao();
    private final BorderPane view = new BorderPane();

    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private final ObservableList<Doctor> doctors = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        build();
        refreshAll();
    }

    public Parent getView() {
        return view;
    }

    private void build() {
        view.getStyleClass().add("dashboard-shell");

        Label product = new Label("Health Assistance System");
        product.getStyleClass().add("app-title");
        Label header = new Label("Signed in as " + user.getUsername() + " - " + roleLabel(user.getRole()));
        header.getStyleClass().add("muted-text");
        Button logout = new Button("Log Out");
        logout.getStyleClass().add("secondary-button");
        logout.setOnAction(event -> {
            Scene scene = new Scene(new LoginView(stage).getView(), 1200, 720);
            UiUtil.applyTheme(scene);
            stage.setScene(scene);
        });

        VBox identity = new VBox(4, product, header);
        HBox top = new HBox(18, identity, spacer(), logout);
        top.getStyleClass().add("top-bar");
        top.setAlignment(Pos.CENTER_LEFT);
        view.setTop(top);

        TabPane tabs = new TabPane();
        if (user.getRole() == Role.ADMINISTRATOR) {
            tabs.getTabs().add(new Tab("Patients", createPatientTab()));
            tabs.getTabs().add(new Tab("Doctors", createDoctorTab()));
            tabs.getTabs().add(new Tab("Appointments", createAppointmentTab()));
            tabs.getTabs().add(new Tab("Users", createUserTab()));
        } else if (user.getRole() == Role.DOCTOR) {
            tabs.getTabs().add(new Tab("Patients", createPatientTab()));
            tabs.getTabs().add(new Tab("Appointments", createAppointmentTab()));
            tabs.getTabs().add(new Tab("Referrals", createReferralTab()));
        } else {
            tabs.getTabs().add(new Tab("My Record", createPatientTab()));
            tabs.getTabs().add(new Tab("Appointments", createAppointmentTab()));
        }
        tabs.getTabs().forEach(tab -> tab.setClosable(false));

        VBox center = new VBox(16, summaryBar(), tabs);
        center.getStyleClass().add("dashboard-content");
        VBox.setVgrow(tabs, Priority.ALWAYS);
        view.setCenter(center);
    }

    private Parent createPatientTab() {
        TableView<Patient> table = new TableView<>(patients);
        table.setPlaceholder(new Label("No patients found."));
        table.getColumns().add(column("Name", Patient::getFullName));
        table.getColumns().add(column("Email", Patient::getEmail));
        table.getColumns().add(column("Phone", Patient::getPhone));
        table.getColumns().add(column("Date of Birth", patient -> patient.getDateOfBirth() == null ? "" : patient.getDateOfBirth().toString()));

        TextField name = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        DatePicker dob = new DatePicker();
        TextField address = new TextField();
        TextArea record = new TextArea();
        record.setPrefRowCount(5);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                return;
            }
            name.setText(selected.getFullName());
            email.setText(selected.getEmail());
            phone.setText(selected.getPhone());
            dob.setValue(selected.getDateOfBirth());
            address.setText(selected.getAddress());
            record.setText(selected.getHealthRecord());
        });

        Button update = new Button("Update Patient");
        update.setOnAction(event -> updatePatient(table, name, email, phone, dob, address, record));
        Button refresh = new Button("Refresh");
        refresh.setOnAction(event -> loadPatients());

        HBox actions;
        if (user.getRole() == Role.ADMINISTRATOR) {
            Button save = new Button("Register Patient");
            save.getStyleClass().add("primary-button");
            save.setOnAction(event -> savePatient(table, name, email, phone, dob, address, record));
            actions = actionRow(save, update, refresh);
        } else {
            update.getStyleClass().add("primary-button");
            actions = actionRow(update, refresh);
        }

        VBox form = formBox(
                formHeader(patientFormTitle(), patientFormCaption()),
                labeled("Full Name", name),
                labeled("Email", email),
                labeled("Phone", phone),
                labeled("Date of Birth", dob),
                labeled("Address", address),
                labeled("Health Record", record),
                actions
        );
        return split(table, form);
    }

    private Parent createDoctorTab() {
        TableView<Doctor> table = new TableView<>(doctors);
        table.setPlaceholder(new Label("No doctors found."));
        table.getColumns().add(column("Name", Doctor::getFullName));
        table.getColumns().add(column("Email", Doctor::getEmail));
        table.getColumns().add(column("Specialization", Doctor::getSpecialization));
        table.getColumns().add(column("Schedule", Doctor::getSchedule));

        TextField name = new TextField();
        TextField email = new TextField();
        TextField specialization = new TextField();
        DatePicker availabilityDate = new DatePicker(LocalDate.now());
        ListView<String> availabilityDays = new ListView<>(FXCollections.observableArrayList());
        availabilityDays.setPrefHeight(118);

        Button addDay = new Button("Add Day");
        addDay.setOnAction(event -> addAvailabilityDay(availabilityDate, availabilityDays));
        Button removeDay = new Button("Remove Selected");
        removeDay.setOnAction(event -> {
            String selectedDay = availabilityDays.getSelectionModel().getSelectedItem();
            if (selectedDay != null) {
                availabilityDays.getItems().remove(selectedDay);
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                return;
            }
            name.setText(selected.getFullName());
            email.setText(selected.getEmail());
            specialization.setText(selected.getSpecialization());
            availabilityDays.getItems().setAll(parseScheduleDays(selected.getSchedule()));
        });

        Button save = new Button("Register Doctor");
        save.getStyleClass().add("primary-button");
        save.setOnAction(event -> saveDoctor(table, name, email, specialization, availabilityDays));
        Button update = new Button("Update Doctor");
        update.setOnAction(event -> updateDoctor(table, name, email, specialization, availabilityDays));
        Button refresh = new Button("Refresh");
        refresh.setOnAction(event -> loadDoctors());

        VBox form = formBox(
                formHeader("Doctor Profile", "Maintain clinical staff details and schedules."),
                labeled("Full Name", name),
                labeled("Email", email),
                labeled("Specialization", specialization),
                labeled("Available Day", availabilityDate),
                new HBox(8, addDay, removeDay),
                labeled("Consultation Days", availabilityDays),
                new HBox(8, save, update, refresh)
        );
        return split(table, form);
    }

    private Parent createAppointmentTab() {
        TableView<Appointment> table = new TableView<>(appointments);
        table.setPlaceholder(new Label("No upcoming booked appointments."));
        table.getColumns().add(column("Patient", Appointment::getPatientName));
        table.getColumns().add(column("Doctor", appointment -> "Dr. " + appointment.getDoctorName()));
        table.getColumns().add(column("Time", appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        table.getColumns().add(column("Reason", Appointment::getReason));
        table.getColumns().add(column("Status", Appointment::getStatus));

        TextField patient = new TextField();
        patient.setPromptText("Type patient name or email");
        if (user.getRole() == Role.PATIENT && !appointmentPatientOptions().isEmpty()) {
            patient.setText(appointmentPatientOptions().get(0).getFullName());
            patient.setDisable(true);
        }
        ComboBox<Doctor> doctor = new ComboBox<>(appointmentDoctorOptions());
        if (user.getRole() == Role.DOCTOR && !doctor.getItems().isEmpty()) {
            doctor.setValue(doctor.getItems().get(0));
            doctor.setDisable(true);
        }
        DatePicker date = new DatePicker(LocalDate.now());
        TextField time = new TextField();
        time.setPromptText("HH:mm");
        TextField reason = new TextField();

        Button book = new Button("Book Appointment");
        book.getStyleClass().add("primary-button");
        book.setMinWidth(150);
        book.setOnAction(event -> bookAppointment(patient, doctor, date, time, reason));
        Button cancel = new Button("Cancel Selected");
        cancel.setMinWidth(130);
        cancel.setOnAction(event -> cancelAppointment(table));
        Button refresh = new Button("Refresh");
        refresh.setMinWidth(90);
        refresh.setOnAction(event -> loadAppointments());

        VBox form = formBox(
                formHeader("Appointment", "Book visits and prevent doctor slot conflicts."),
                labeled("Patient", patient),
                labeled("Doctor", doctor),
                labeled("Date", date),
                labeled("Time", time),
                labeled("Reason", reason),
                actionRow(book, cancel, refresh)
        );
        return split(table, form);
    }

    private Parent createReferralTab() {
        TextField patient = new TextField();
        patient.setPromptText("Type patient name or email");
        ComboBox<Doctor> targetDoctor = new ComboBox<>(doctors);
        DatePicker date = new DatePicker(LocalDate.now());
        TextField time = new TextField();
        time.setPromptText("HH:mm");
        TextArea reason = new TextArea();
        reason.setPrefRowCount(5);
        reason.setPromptText("Reason for advanced consultation");

        Button refer = new Button("Refer Patient");
        refer.getStyleClass().add("primary-button");
        refer.setOnAction(event -> referPatient(patient, targetDoctor, date, time, reason));

        VBox box = formBox(
                formHeader("Refer Patient", "Send one of your patients to another doctor for advanced consultation."),
                labeled("Patient", patient),
                labeled("Target Doctor", targetDoctor),
                labeled("Date", date),
                labeled("Time", time),
                labeled("Reason", reason),
                refer
        );
        box.setPadding(new Insets(24));
        return box;
    }

    private Parent createUserTab() {
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        ComboBox<Role> role = new ComboBox<>(FXCollections.observableArrayList(Role.values()));
        role.setValue(Role.PATIENT);

        Button create = new Button("Create User");
        create.getStyleClass().add("primary-button");
        create.setOnAction(event -> {
            try {
                if (username.getText().isBlank() || password.getText().isBlank()) {
                    UiUtil.showValidation("Username and password are required.");
                    return;
                }
                userDao.createUser(username.getText().trim(), password.getText(), role.getValue());
                username.clear();
                password.clear();
                UiUtil.showInfo("User created", "The user account was created successfully.");
            } catch (SQLException e) {
                UiUtil.showError("Could not create user", e);
            }
        });

        VBox box = formBox(
                formHeader("Access Management", "Create accounts. Use each doctor or patient's email as their username."),
                labeled("Username", username),
                labeled("Password", password),
                labeled("Role", role),
                create
        );
        box.setPadding(new Insets(20));
        return box;
    }

    private void savePatient(TableView<Patient> table, TextField name, TextField email, TextField phone,
                             DatePicker dob, TextField address, TextArea record) {
        try {
            validateRequired(name, email);
            patientDao.save(new Patient(0, name.getText().trim(), email.getText().trim(), phone.getText().trim(),
                    dob.getValue(), address.getText().trim(), record.getText().trim()));
            clearSelection(table);
            refreshAll();
        } catch (Exception e) {
            showFailure("Could not save patient", e);
        }
    }

    private void updatePatient(TableView<Patient> table, TextField name, TextField email, TextField phone,
                               DatePicker dob, TextField address, TextArea record) {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtil.showValidation("Select a patient to update.");
            return;
        }
        try {
            validateRequired(name, email);
            selected.setFullName(name.getText().trim());
            selected.setEmail(email.getText().trim());
            selected.setPhone(phone.getText().trim());
            selected.setDateOfBirth(dob.getValue());
            selected.setAddress(address.getText().trim());
            selected.setHealthRecord(record.getText().trim());
            patientDao.update(selected);
            refreshAll();
        } catch (Exception e) {
            showFailure("Could not update patient", e);
        }
    }

    private void saveDoctor(TableView<Doctor> table, TextField name, TextField email, TextField specialization,
                            ListView<String> availabilityDays) {
        try {
            validateRequired(name, email, specialization);
            validateAvailability(availabilityDays);
            doctorDao.save(new Doctor(0, name.getText().trim(), email.getText().trim(),
                    specialization.getText().trim(), formatScheduleDays(availabilityDays)));
            clearSelection(table);
            availabilityDays.getItems().clear();
            refreshAll();
        } catch (Exception e) {
            showFailure("Could not save doctor", e);
        }
    }

    private void updateDoctor(TableView<Doctor> table, TextField name, TextField email, TextField specialization,
                              ListView<String> availabilityDays) {
        Doctor selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtil.showValidation("Select a doctor to update.");
            return;
        }
        try {
            validateRequired(name, email, specialization);
            validateAvailability(availabilityDays);
            selected.setFullName(name.getText().trim());
            selected.setEmail(email.getText().trim());
            selected.setSpecialization(specialization.getText().trim());
            selected.setSchedule(formatScheduleDays(availabilityDays));
            doctorDao.update(selected);
            refreshAll();
        } catch (Exception e) {
            showFailure("Could not update doctor", e);
        }
    }

    private void bookAppointment(TextField patient, ComboBox<Doctor> doctor, DatePicker date,
                                 TextField time, TextField reason) {
        try {
            Optional<Patient> selectedPatient = findPatientFromInput(patient.getText());
            if (selectedPatient.isEmpty() || doctor.getValue() == null || date.getValue() == null || time.getText().isBlank()) {
                UiUtil.showValidation("Patient, doctor, date, and time are required.");
                return;
            }
            LocalDateTime appointmentTime = LocalDateTime.of(date.getValue(), LocalTime.parse(time.getText().trim()));
            appointmentDao.book(selectedPatient.get().getId(), doctor.getValue().getId(), appointmentTime, reason.getText().trim());
            reason.clear();
            loadAppointments();
            UiUtil.showInfo("Appointment booked", "The appointment was booked successfully.");
        } catch (Exception e) {
            showFailure("Could not book appointment", e);
        }
    }

    private void referPatient(TextField patient, ComboBox<Doctor> targetDoctor, DatePicker date,
                              TextField time, TextArea reason) {
        try {
            Optional<Doctor> currentDoctor = currentDoctor();
            if (currentDoctor.isEmpty()) {
                UiUtil.showValidation("Your doctor account is not linked. Log in with the same email recorded in the Doctors table.");
                return;
            }
            Optional<Patient> selectedPatient = findPatientFromInput(patient.getText());
            if (selectedPatient.isEmpty() || targetDoctor.getValue() == null || date.getValue() == null || time.getText().isBlank()) {
                UiUtil.showValidation("Patient, target doctor, date, and time are required.");
                return;
            }
            if (targetDoctor.getValue().getId() == currentDoctor.get().getId()) {
                UiUtil.showValidation("Select a different doctor for the referral.");
                return;
            }
            LocalDateTime appointmentTime = LocalDateTime.of(date.getValue(), LocalTime.parse(time.getText().trim()));
            String referralReason = "Referral from Dr. " + currentDoctor.get().getFullName() + ": " + reason.getText().trim();
            appointmentDao.book(selectedPatient.get().getId(), targetDoctor.getValue().getId(), appointmentTime, referralReason);
            reason.clear();
            time.clear();
            UiUtil.showInfo("Referral created", "The patient was referred and an appointment was booked with the target doctor.");
        } catch (Exception e) {
            showFailure("Could not refer patient", e);
        }
    }

    private void cancelAppointment(TableView<Appointment> table) {
        Appointment selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtil.showValidation("Select an appointment to cancel.");
            return;
        }
        if (user.getRole() == Role.PATIENT) {
            try {
                Optional<Patient> currentPatient = currentPatient();
                if (currentPatient.isEmpty() || selected.getPatientId() != currentPatient.get().getId()) {
                    UiUtil.showValidation("You can only cancel your own appointments.");
                    return;
                }
            } catch (SQLException e) {
                UiUtil.showError("Could not verify patient account", e);
                return;
            }
        }
        try {
            appointmentDao.cancel(selected.getId());
            loadAppointments();
        } catch (SQLException e) {
            UiUtil.showError("Could not cancel appointment", e);
        }
    }

    private void refreshAll() {
        loadPatients();
        loadDoctors();
        loadAppointments();
    }

    private void loadPatients() {
        try {
            if (user.getRole() == Role.DOCTOR) {
                Optional<Doctor> doctor = currentDoctor();
                patients.setAll(doctor.isEmpty() ? java.util.List.of() : patientDao.findByDoctorId(doctor.get().getId()));
            } else if (user.getRole() == Role.PATIENT) {
                Optional<Patient> patient = currentPatient();
                patients.setAll(patient.isEmpty() ? java.util.List.of() : java.util.List.of(patient.get()));
            } else {
                patients.setAll(patientDao.findAll());
            }
        } catch (SQLException e) {
            UiUtil.showError("Could not load patients", e);
        }
    }

    private void loadDoctors() {
        try {
            doctors.setAll(doctorDao.findAll());
        } catch (SQLException e) {
            UiUtil.showError("Could not load doctors", e);
        }
    }

    private void loadAppointments() {
        try {
            if (user.getRole() == Role.DOCTOR) {
                Optional<Doctor> doctor = currentDoctor();
                appointments.setAll(doctor.isEmpty() ? java.util.List.of() : appointmentDao.findUpcomingByDoctorId(doctor.get().getId()));
            } else if (user.getRole() == Role.PATIENT) {
                Optional<Patient> patient = currentPatient();
                appointments.setAll(patient.isEmpty() ? java.util.List.of() : appointmentDao.findUpcomingByPatientId(patient.get().getId()));
            } else {
                appointments.setAll(appointmentDao.findUpcoming());
            }
        } catch (SQLException e) {
            UiUtil.showError("Could not load appointments", e);
        }
    }

    private Optional<Doctor> currentDoctor() throws SQLException {
        if (user.getRole() != Role.DOCTOR) {
            return Optional.empty();
        }
        return doctorDao.findByEmail(user.getUsername());
    }

    private Optional<Patient> currentPatient() throws SQLException {
        if (user.getRole() != Role.PATIENT) {
            return Optional.empty();
        }
        return patientDao.findByEmailOrName(user.getUsername());
    }

    private ObservableList<Patient> appointmentPatientOptions() {
        if (user.getRole() != Role.PATIENT) {
            return patients;
        }
        try {
            Optional<Patient> patient = currentPatient();
            return FXCollections.observableArrayList(patient.stream().toList());
        } catch (SQLException e) {
            UiUtil.showError("Could not load patient profile", e);
            return FXCollections.observableArrayList();
        }
    }

    private Optional<Patient> findPatientFromInput(String input) {
        if (user.getRole() == Role.PATIENT) {
            try {
                return currentPatient();
            } catch (SQLException e) {
                UiUtil.showError("Could not load patient profile", e);
                return Optional.empty();
            }
        }
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        String normalizedInput = input.trim().toLowerCase();
        return patients.stream()
                .filter(patient -> patient.getFullName().equalsIgnoreCase(input.trim())
                        || patient.getEmail().equalsIgnoreCase(input.trim())
                        || patient.getFullName().toLowerCase().contains(normalizedInput))
                .findFirst();
    }

    private ObservableList<Doctor> appointmentDoctorOptions() {
        if (user.getRole() != Role.DOCTOR) {
            return doctors;
        }
        try {
            Optional<Doctor> doctor = currentDoctor();
            return FXCollections.observableArrayList(doctor.stream().toList());
        } catch (SQLException e) {
            UiUtil.showError("Could not load doctor profile", e);
            return FXCollections.observableArrayList();
        }
    }

    private void validateRequired(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText() == null || field.getText().isBlank()) {
                throw new IllegalArgumentException("Please fill all required text fields.");
            }
        }
    }

    private void validateAvailability(ListView<String> availabilityDays) {
        if (availabilityDays.getItems().isEmpty()) {
            throw new IllegalArgumentException("Select at least one consultation day for the doctor.");
        }
    }

    private void addAvailabilityDay(DatePicker availabilityDate, ListView<String> availabilityDays) {
        LocalDate selectedDate = availabilityDate.getValue();
        if (selectedDate == null) {
            UiUtil.showValidation("Select an available consultation day.");
            return;
        }
        String day = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (!availabilityDays.getItems().contains(day)) {
            availabilityDays.getItems().add(day);
            FXCollections.sort(availabilityDays.getItems());
        }
    }

    private String formatScheduleDays(ListView<String> availabilityDays) {
        return String.join(", ", availabilityDays.getItems());
    }

    private java.util.List<String> parseScheduleDays(String schedule) {
        if (schedule == null || schedule.isBlank()) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(schedule.split(","))
                .map(String::trim)
                .filter(day -> !day.isBlank())
                .toList();
    }

    private void showFailure(String title, Exception e) {
        if (e instanceof IllegalArgumentException) {
            UiUtil.showValidation(e.getMessage());
        } else {
            UiUtil.showError(title, e);
        }
    }

    private <T> TableColumn<T, String> column(String title, java.util.function.Function<T, String> mapper) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        column.setPrefWidth(170);
        return column;
    }

    private HBox spacer() {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private VBox formBox(javafx.scene.Node... nodes) {
        VBox box = new VBox(10, nodes);
        box.getStyleClass().add("side-form");
        box.setPadding(new Insets(16));
        box.setPrefWidth(430);
        return box;
    }

    private HBox actionRow(Button... buttons) {
        HBox row = new HBox(10, buttons);
        row.setAlignment(Pos.CENTER_LEFT);
        for (Button button : buttons) {
            button.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(button, Priority.ALWAYS);
        }
        return row;
    }

    private GridPane labeled(String label, javafx.scene.Node input) {
        GridPane pane = new GridPane();
        pane.getStyleClass().add("field-row");
        pane.setHgap(8);
        pane.setVgap(4);
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("field-label");
        labelNode.setMinWidth(100);
        pane.add(labelNode, 0, 0);
        pane.add(input, 1, 0);
        return pane;
    }

    private Parent split(TableView<?> table, VBox form) {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("workspace");
        pane.setCenter(table);
        pane.setRight(form);
        return pane;
    }

    private void clearSelection(TableView<?> table) {
        table.getSelectionModel().clearSelection();
    }

    private HBox summaryBar() {
        HBox bar = new HBox(14,
                summaryCard("Patients", patients, "registered records"),
                summaryCard("Doctors", doctors, "clinical providers"),
                summaryCard("Appointments", appointments, "upcoming visits"),
                summaryCard("Role", roleLabel(user.getRole()), "current access")
        );
        bar.getStyleClass().add("summary-bar");
        return bar;
    }

    private VBox summaryCard(String title, ObservableList<?> values, String caption) {
        VBox card = summaryCard(title, "", caption);
        Label valueLabel = (Label) card.getChildren().get(1);
        valueLabel.textProperty().bind(Bindings.size(values).asString());
        return card;
    }

    private VBox summaryCard(String title, String value, String caption) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("summary-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("summary-value");
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("summary-caption");
        VBox card = new VBox(4, titleLabel, valueLabel, captionLabel);
        card.getStyleClass().add("summary-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox formHeader(String title, String caption) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("form-title");
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("muted-text");
        captionLabel.setWrapText(true);
        return new VBox(4, titleLabel, captionLabel);
    }

    private String patientFormTitle() {
        return switch (user.getRole()) {
            case ADMINISTRATOR -> "Patient Profile";
            case DOCTOR -> "Assigned Patient Record";
            case PATIENT -> "My Health Record";
        };
    }

    private String patientFormCaption() {
        return switch (user.getRole()) {
            case ADMINISTRATOR -> "Register, view, update, and access patient health records.";
            case DOCTOR -> "View and update records for patients assigned through appointments.";
            case PATIENT -> "View and maintain your own patient information and health record.";
        };
    }

    private String roleLabel(Role role) {
        return switch (role) {
            case ADMINISTRATOR -> "Administrator";
            case DOCTOR -> "Doctor";
            case PATIENT -> "Patient";
        };
    }
}
