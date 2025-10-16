package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Comment;

import java.util.List;

public interface CommentService{

    List<Comment> getCommentsByPostId(Long postId);
    Comment saveComment(Comment comment);
    void deleteComment(Long id);
    Comment findById(Long id);

    Comment getCommentById(Long commentId);
}
