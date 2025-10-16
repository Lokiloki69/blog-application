package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Comment;
import com.example.BlogApplication.repo.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentRepo commentRepo;

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepo.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepo.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepo.deleteById(id);
    }

    @Override
    public Comment findById(Long id) {
        return commentRepo.findById(id).orElse(null);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepo.findById(id).orElse(null);
    }
}
