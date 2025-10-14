package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PostService {
    void savePost(Post post,String tagString);
    Post getPostById(Long id);
    List<Post> getAllPosts(Sort sort);
    void deletePostById(Long id);

    Page<Post> getAllPosts(Pageable pageable);
    Page<Post> searchPosts(String query, Pageable pageable);
    Page<Post> getPostsByAuthorAndTags(List<Long> authorIds, List<Long> tagIds,Pageable pageable);
}
