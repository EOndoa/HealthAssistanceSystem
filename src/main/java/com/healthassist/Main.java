package com.healthassist;

import com.healthassist.service.AppointmentReminderService;
import com.healthassist.ui.LoginView;
import com.healthassist.ui.UiUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private final AppointmentReminderService reminderService = new AppointmentReminderService();

    @Override
    public void start(Stage stage) {
        reminderService.start();
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getView(), 1200, 720);
        UiUtil.applyTheme(scene);
        stage.setTitle("Health Assistance System");
        stage.setMinWidth(1100);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        reminderService.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
