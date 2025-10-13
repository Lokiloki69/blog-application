package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Comment;

import java.util.List;

public interface CommentService{
    List<Comment> getCommentsByPostId(Long postId);
    Comment getCommentById(Long postId);
    void saveComment(Comment comment);
    void updateComment(Comment comment);
    void deleteComment(Long id);
}
