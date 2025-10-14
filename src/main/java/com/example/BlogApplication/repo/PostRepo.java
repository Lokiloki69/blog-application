package com.example.BlogApplication.repo;

import com.example.BlogApplication.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepo extends JpaRepository<Post,Long>{

    @Query("SELECT DISTINCT p from Post p LEFT JOIN p.tags t " +
    "WHERE LOWER(p.title) LIKE LOWER(:query) " +
    "OR LOWER(t.name) LIKE LOWER(:query)" +
    "OR LOWER(p.content) LIKE LOWER(:query)" +
    "OR LOWER(p.user.name) LIKE LOWER(:query)" +
    "OR LOWER(p.excerpt) LIKE LOWER(:query)")
    Page<Post> searchByTitleContentAuthorTags(String query, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p WHERE p.user.id IN :userIds")
    Page<Post> findDistinctByUserIds(@Param("userIds") List<Long> userIds, Pageable pageable);

    Page<Post> findDistinctByTags_IdIn(List<Long> tagIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t " +
            "WHERE (:userIds IS NULL OR p.user.id IN :userIds) " +
            "AND (:tagIds IS NULL OR t.id IN :tagIds)")
    Page<Post> findByUsersAndTags(@Param("userIds") List<Long> userids,
                                  @Param("tagIds") List<Long> tagIds,
                                  Pageable pageable);


}
