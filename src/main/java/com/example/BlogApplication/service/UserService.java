package com.example.BlogApplication.service;

import com.example.BlogApplication.model.User;

import java.util.List;

public interface UserService {
    User findById(Long id);

    List<User> getAllUsers();
}
