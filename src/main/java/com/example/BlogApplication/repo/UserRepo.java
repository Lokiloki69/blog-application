package com.example.BlogApplication.repo;

import com.example.BlogApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long>{
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String username);
}
