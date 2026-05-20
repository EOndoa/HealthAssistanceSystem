package com.healthassist.service;

import com.healthassist.dao.UserDao;
import com.healthassist.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public Optional<User> login(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        return userDao.authenticate(username.trim(), password);
    }
}
