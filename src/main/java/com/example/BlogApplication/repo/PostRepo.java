package com.example.BlogApplication.repo;

import com.example.BlogApplication.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepo extends JpaRepository<Post,Long>{

    @Query("SELECT DISTINCT p from Post p LEFT JOIN p.tags t " +
    "WHERE LOWER(p.title) LIKE LOWER(:query) " +
    "OR LOWER(t.name) LIKE LOWER(:query)" +
    "OR LOWER(p.content) LIKE LOWER(:query)" +
    "OR LOWER(p.user.name) LIKE LOWER(:query)" +
    "OR LOWER(p.excerpt) LIKE LOWER(:query)")
    List<Post> findBySearchQuery(String query, Sort sort);

    List<Post> findDistinctByUser_Id(Long userid);
    List<Post> findDistinctByTags_IdIn(List<Long> tagIds);
    List<Post> findDistinctByUser_IdAndTags_IdIn(Long userId, List<Long> tagIds);

}
