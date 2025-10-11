package com.example.BlogApplication.controller;

import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.service.PostService;
import com.example.BlogApplication.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController{

    private final PostService postService;
    private final TagService tagService;

    @Autowired
    public PostController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/newpost")
    public String showPostForm(Model model) {
        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
        model.addAttribute("tags", tagService.findAll());
        return "newPost";
    }



}
