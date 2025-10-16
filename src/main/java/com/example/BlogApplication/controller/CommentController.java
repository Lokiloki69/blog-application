package com.example.BlogApplication.controller;

import com.example.BlogApplication.model.Comment;
import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.model.User;
import com.example.BlogApplication.service.CommentService;
import com.example.BlogApplication.service.PostService;
import com.example.BlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable Long postId,
                             @RequestParam("content") String content,
                             @RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "email", required = false) String email) {

        Post post = postService.getPostById(postId);
        if (post == null) {
            return "redirect:/";
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User user = userService.findByEmail(auth.getName());
            comment.setUser(user);
            comment.setName(user.getName());
            comment.setEmail(user.getEmail());
        } else {
            comment.setName(name != null ? name : "Anonymous");
            comment.setEmail(email);
        }

        commentService.saveComment(comment);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/edit/{id}")
    public String editComment(@PathVariable("id") Long id, Model model){
        Comment comment = commentService.getCommentById(id);
        if(comment == null){
            return "redirect:/home";
        }
        model.addAttribute(comment);
        return "editComment";
    }

    @PostMapping("/update/{commentId}")
    public String editComment(@PathVariable Long commentId,
                              @RequestParam("content") String updatedContent) {

        Comment existing = commentService.getCommentById(commentId);
        if (existing == null) return "redirect:/";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String currentEmail = auth.getName();

            boolean canEdit = existing.getPost().getUser().getEmail().equals(currentEmail) ||
                    (existing.getUser() != null && existing.getUser().getEmail().equals(currentEmail));

            if (canEdit) {
                existing.setContent(updatedContent);
                commentService.saveComment(existing);
            }
        }

        return "redirect:/post/" + existing.getPost().getId();
    }

    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        Comment existing = commentService.getCommentById(commentId);
        if (existing == null) return "redirect:/";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String currentEmail = auth.getName();

            boolean canDelete = existing.getPost().getUser().getEmail().equals(currentEmail) ||
                    (existing.getUser() != null && existing.getUser().getEmail().equals(currentEmail));

            if (canDelete) {
                commentService.deleteComment(commentId);
            }
        }

        return "redirect:/post/" + existing.getPost().getId();
    }
}
