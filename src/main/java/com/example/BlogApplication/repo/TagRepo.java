package com.example.BlogApplication.repo;

import com.example.BlogApplication.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag,Long>{
    Tag findByName(String name);
}
