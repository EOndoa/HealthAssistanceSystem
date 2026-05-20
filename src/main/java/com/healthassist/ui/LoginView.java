package com.healthassist.ui;

import com.healthassist.model.User;
import com.healthassist.service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class LoginView {
    private final Stage stage;
    private final AuthService authService = new AuthService();
    private final StackPane view = new StackPane();

    public LoginView(Stage stage) {
        this.stage = stage;
        build();
    }

    public Parent getView() {
        return view;
    }

    private void build() {
        view.getStyleClass().add("login-shell");
        view.getChildren().addAll(decorativeBackground(), mainContent());
    }

    private BorderPane mainContent() {
        BorderPane content = new BorderPane();
        content.getStyleClass().add("welcome-content");
        content.setMaxSize(1080, 650);

        VBox intro = new VBox(18, brandLockup(), welcomeBlock(), divider(), bodyCopy());
        intro.getStyleClass().add("welcome-intro");

        VBox rightRail = new VBox(14, photoPanel(), assuranceCard(), signInPanel());
        rightRail.getStyleClass().add("welcome-right-rail");

        content.setLeft(intro);
        content.setRight(rightRail);
        content.setBottom(featureStrip());
        return content;
    }

    private Pane decorativeBackground() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);

        Circle outer = new Circle(320);
        outer.getStyleClass().add("hero-ring-outer");
        outer.setLayoutX(910);
        outer.setLayoutY(190);

        Circle inner = new Circle(245);
        inner.getStyleClass().add("hero-ring-inner");
        inner.setLayoutX(910);
        inner.setLayoutY(190);

        Circle wash = new Circle(430);
        wash.getStyleClass().add("hero-bottom-wash");
        wash.setLayoutX(760);
        wash.setLayoutY(675);

        pane.getChildren().addAll(wash, outer, inner);
        return pane;
    }

    private HBox brandLockup() {
        StackPane mark = new StackPane();
        mark.getStyleClass().add("brand-mark");
        Label markText = new Label("H");
        markText.getStyleClass().add("brand-mark-text");
        mark.getChildren().add(markText);

        Label health = new Label("HEALTH");
        health.getStyleClass().add("brand-health");
        Label assistance = new Label("ASSISTANCE");
        assistance.getStyleClass().add("brand-assistance");
        Label system = new Label("SYSTEM");
        system.getStyleClass().add("brand-system");
        VBox wordmark = new VBox(0, health, assistance, system);

        HBox lockup = new HBox(18, mark, wordmark);
        lockup.setAlignment(Pos.CENTER_LEFT);
        return lockup;
    }

    private VBox welcomeBlock() {
        Label welcome = new Label("WELCOME");
        welcome.getStyleClass().add("welcome-title");
        Label subtitle = new Label("to the Health Assistance System");
        subtitle.getStyleClass().add("welcome-subtitle");
        return new VBox(4, welcome, subtitle);
    }

    private HBox divider() {
        Region leftLine = new Region();
        leftLine.getStyleClass().add("divider-line");
        Region rightLine = new Region();
        rightLine.getStyleClass().add("divider-line");
        Label heart = new Label("H");
        heart.getStyleClass().add("divider-heart");
        HBox divider = new HBox(14, leftLine, heart, rightLine);
        divider.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftLine, Priority.ALWAYS);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        divider.setMaxWidth(520);
        return divider;
    }

    private VBox bodyCopy() {
        Label copy = new Label("""
                Your health and well-being are our top priority.
                Our system is designed to provide you with seamless access to quality care,
                secure information, and support every step of the way.
                """);
        copy.getStyleClass().add("welcome-copy");
        copy.setWrapText(true);

        Label promise = new Label("Better care. Better health. Better together.");
        promise.getStyleClass().add("welcome-promise");
        return new VBox(22, copy, promise);
    }

    private StackPane photoPanel() {
        Label photoText = new Label("Compassionate care");
        photoText.getStyleClass().add("photo-panel-title");
        StackPane panel = new StackPane(photoText);
        panel.getStyleClass().add("photo-panel");
        return panel;
    }

    private HBox assuranceCard() {
        Label panelTitle = new Label("Safe. Secure. Confidential.");
        panelTitle.getStyleClass().add("assurance-title");
        Label panelCopy = new Label("We protect privacy while helping teams deliver trusted, high-quality healthcare services.");
        panelCopy.getStyleClass().add("assurance-copy");
        panelCopy.setWrapText(true);

        StackPane shield = new StackPane(new Label("OK"));
        shield.getStyleClass().add("shield-mark");

        HBox assurance = new HBox(16, shield, new VBox(4, panelTitle, panelCopy));
        assurance.getStyleClass().add("assurance-card");
        assurance.setAlignment(Pos.CENTER_LEFT);
        return assurance;
    }

    private VBox signInPanel() {
        Label formTitle = new Label("Secure Sign In");
        formTitle.getStyleClass().add("login-card-title");
        Label formSubTitle = new Label("Access your healthcare workspace.");
        formSubTitle.getStyleClass().add("login-card-subtitle");
        formSubTitle.setWrapText(true);

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        GridPane form = new GridPane();
        form.getStyleClass().add("login-form-grid");
        form.setHgap(10);
        form.setVgap(10);
        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("field-label");
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");
        form.addRow(0, usernameLabel, username);
        form.addRow(1, passwordLabel, password);

        Button login = new Button("Log In");
        login.getStyleClass().add("primary-button");
        login.setDefaultButton(true);
        login.setMaxWidth(Double.MAX_VALUE);
        login.setOnAction(event -> openDashboard(username.getText(), password.getText()));

        VBox panel = new VBox(12, formTitle, formSubTitle, form, login);
        panel.getStyleClass().add("auth-card");
        return panel;
    }

    private HBox featureStrip() {
        HBox strip = new HBox(0,
                feature("Patient Centered", "Your health journey is our priority."),
                feature("Easy Access", "Connect with care whenever you need it."),
                feature("Secure & Private", "Your information is always protected."),
                feature("Better Outcomes", "Tools and support for a healthier you."),
                feature("Stronger Together", "Partnering for healthier communities.")
        );
        strip.getStyleClass().add("feature-strip");
        return strip;
    }

    private HBox feature(String title, String description) {
        StackPane icon = new StackPane(new Label("+"));
        icon.getStyleClass().add("feature-icon");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("feature-title");
        titleLabel.setWrapText(true);
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("feature-description");
        descriptionLabel.setWrapText(true);
        VBox text = new VBox(4, titleLabel, descriptionLabel);
        text.getStyleClass().add("feature-text");
        HBox card = new HBox(12, icon, text);
        card.getStyleClass().add("feature-card");
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void openDashboard(String username, String password) {
        try {
            Optional<User> user = authService.login(username, password);
            if (user.isEmpty()) {
                UiUtil.showValidation("Invalid username or password.");
                return;
            }
            DashboardView dashboard = new DashboardView(stage, user.get());
            Scene scene = new Scene(dashboard.getView(), 1180, 760);
            UiUtil.applyTheme(scene);
            stage.setScene(scene);
        } catch (SQLException e) {
            UiUtil.showError("Login failed", e);
        }
    }
}
