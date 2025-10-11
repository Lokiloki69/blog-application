package com.example.BlogApplication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonBackReference
    @ManyToOne
    private Post post;
    private String name;
    private String email;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}