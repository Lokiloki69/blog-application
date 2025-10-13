package com.example.BlogApplication.controller;

import com.example.BlogApplication.model.Comment;
import com.example.BlogApplication.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController{
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable("id") Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment != null && comment.getPost() != null) {
            Long postId = comment.getPost().getId();
            commentService.deleteComment(id);
            return "redirect:/post/" + postId;
        }
        return "redirect:/home";
    }

    @GetMapping("/comment/edit/{id}")
    public String editComment(@PathVariable("id") Long id, Model model){
        Comment comment = commentService.getCommentById(id);
        if(comment == null){
            return "redirect:/home";
        }
        model.addAttribute(comment);
        return "editComment";
    }

    @PostMapping("/comment/update/{commentId}")
    public String updateComment(@PathVariable("commentId") Long id,@ModelAttribute("comment") Comment updatedComment){

        Comment existingComment = commentService.getCommentById(id);
        if(existingComment != null)
        {
            existingComment.setName(updatedComment.getName());
            existingComment.setEmail(updatedComment.getEmail());
            existingComment.setCommentContent(updatedComment.getCommentContent());
            commentService.updateComment(existingComment);

            return "redirect:/post/" + existingComment.getPost().getId();
        }
        return "redirect:/home";
    }
}
