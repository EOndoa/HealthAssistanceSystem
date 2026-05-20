package com.healthassist.dao;

import com.healthassist.db.Database;
import com.healthassist.model.Role;
import com.healthassist.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {
    public Optional<User> authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(new User(
                            result.getInt("id"),
                            result.getString("username"),
                            Role.valueOf(result.getString("role"))
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void createUser(String username, String password, Role role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role.name());
            statement.executeUpdate();
        }
    }
}
