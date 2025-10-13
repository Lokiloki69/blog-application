package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Post;
import java.util.List;

public interface PostService {
    void savePost(Post post,String tagString);
    Post getPostById(Long id);
    List<Post> getAllPosts();
    void deletePostById(Long id);

    List<Post> searchPosts(String query);
}
