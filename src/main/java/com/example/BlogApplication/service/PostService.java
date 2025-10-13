package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Post;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PostService {
    void savePost(Post post,String tagString);
    Post getPostById(Long id);
    List<Post> getAllPosts(Sort sort);
    void deletePostById(Long id);

//    List<Post> searchPosts(String query);
    List<Post> searchPosts(String query,Sort sort);

    List<Post> getPostsByAuthorAndTags(Long authorId, List<Long> tagIds,Sort sort);
}
