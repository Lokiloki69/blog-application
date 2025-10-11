package com.example.BlogApplication.service;

import com.example.BlogApplication.dto.PostRequest;
import com.example.BlogApplication.model.Post;

import java.util.List;

public interface PostService{
    Post creatPost(Post post);
    List<Post> getAllPosts();
    Post getPostById(Long id);

    Post createPostWithTags(PostRequest postRequest);

    Post updatePost(Long id, PostRequest postRequest);
}
