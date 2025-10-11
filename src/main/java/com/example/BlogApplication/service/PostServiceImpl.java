package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.model.Tag;
import com.example.BlogApplication.model.User;
import com.example.BlogApplication.repo.PostRepo;
import com.example.BlogApplication.repo.TagRepo;
import com.example.BlogApplication.repo.UserRepo;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements  PostService{

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TagRepo tagRepo;

}
