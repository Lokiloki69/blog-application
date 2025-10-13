package com.example.BlogApplication.controller;

import com.example.BlogApplication.model.Comment;
import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.model.Tag;
import com.example.BlogApplication.model.User;
import com.example.BlogApplication.service.CommentService;
import com.example.BlogApplication.service.PostService;
import com.example.BlogApplication.service.TagService;
import com.example.BlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, TagService tagService, UserService userService,CommentService commentService) {
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
        this.commentService= commentService;
    }

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/home";
    }

    @GetMapping("/newpost")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("tagString", "");
        return "newPost";
    }

    @PostMapping("/savePost")
    public String savePost(@ModelAttribute("post") Post post,
                           @RequestParam("tagString") String tagString) {
        User user = userService.findById(1L);
        post.setUser(user);
        postService.savePost(post, tagString);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String listPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "home";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/home";
        }
        List<Comment> comments =commentService.getCommentsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments",comments);
        model.addAttribute("newComment",new Comment());
        return "viewPost";
    }

    @GetMapping("/editPost/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/home";
        }

        String tagString = post.getTags().stream()
                .map(Tag::getName)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        model.addAttribute("post", post);
        model.addAttribute("tagString", tagString);
        return "newPost";
    }

    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute("post") Post post,
                             @RequestParam("tagString") String tagString) {

        User user = userService.findById(1L);
        post.setUser(user);
        postService.savePost(post, tagString);
        return "redirect:/home";
    }

    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return "redirect:/home";
    }

    @PostMapping("/post/{id}/comment")
    public String addComment(@PathVariable("id") Long postId,
                             @ModelAttribute("newComment") Comment comment) {

        Post post = postService.getPostById(postId);
//        comment.setId(null);
        comment.setPost(post);
        commentService.saveComment(comment);
        return "redirect:/post/" + postId;
    }


    @GetMapping("/home/search")
    public String searchPosts(@RequestParam("query") String query,Model model){
        List<Post> result = postService.searchPosts(query);
        model.addAttribute("posts", result);
        model.addAttribute("searchQuery",query);

        return "home";

    }
}
