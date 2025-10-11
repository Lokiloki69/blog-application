package com.example.BlogApplication.repo;

import com.example.BlogApplication.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepo extends JpaRepository<Tag,Long>{
    Optional<Tag> findByName(String name);
}
