package com.example.BlogApplication.controller;

import com.example.BlogApplication.dto.PostRequest;
import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController{

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequest postRequest){
        Post savedPost = postService.createPostWithTags(postRequest);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(){
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id){
        Post post = postService.getPostById(id);
        if(post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(post,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePostbyId(@PathVariable Long id, PostRequest postRequest)
    {
        Post updated = postService.updatePost(id,postRequest);
        return ResponseEntity.ok(updated);
    }
}
