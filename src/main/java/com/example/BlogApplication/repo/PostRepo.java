package com.example.BlogApplication.repo;

import com.example.BlogApplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post,Long>{
}
