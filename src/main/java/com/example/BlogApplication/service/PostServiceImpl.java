package com.example.BlogApplication.service;

import com.example.BlogApplication.dto.PostRequest;
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

    @Override
    public Post creatPost(Post post) {
        if(post.getExcerpt() == null || post.getExcerpt().isEmpty()){
            String content = post.getContent();
            post.setExcerpt(content.length()>150?content.substring(0,150)+"...":content);
        }
        post.setPublished(true);
        post.setPublishedAt(java.time.LocalDateTime.now());
        return postRepo.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        Optional<Post> optionalPost = postRepo.findById(id);
        return optionalPost.orElse(null);
    }

    @Override
    public Post createPostWithTags(PostRequest postRequest) {
        User author = userRepo.findById(postRequest.getAuthorId())
                .orElseThrow(()->new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());

        if(post.getContent().length() > 150){
            post.setExcerpt(post.getContent().substring(0,150)+"...");
        }
        else {
            post.setExcerpt(post.getContent());
        }

        post.setAuthor(author);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Set<Tag> postTags = new HashSet<>();
        if(postRequest.getTags()!=null && !postRequest.getTags().isEmpty()){
            String[] tagNames= postRequest.getTags().split(",");
            for(String name:tagNames)
            {
                name=name.trim().toLowerCase();
                Tag tag= tagRepo.findByName(name);
                if(tag == null)
                {
                    tag = new Tag();
                    tag.setName(name);
                    tag = tagRepo.save(tag);
                }
                postTags.add(tag);
            }
        }
        post.setTags(postTags);

        return postRepo.save(post);
    }

    @Override
    public Post updatePost(Long id, PostRequest postRequest) {
        Post post = postRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Post not found with id: "+id));

        if(postRequest.getTitle() != null && !postRequest.getTitle().isEmpty()){
            post.setTitle(postRequest.getTitle());
        }

        if(postRequest.getContent()!=null && !postRequest.getContent().isEmpty()){
            post.setContent(postRequest.getContent());
        }

        if(postRequest.getAuthorId() != null)
        {
            User author = userRepo.findById(postRequest.getAuthorId())
                    .orElseThrow(()->new RuntimeException("Author not found"));
            post.setAuthor(author);
        }

        if(postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
            Set<Tag> postTags = new HashSet<>();
            if(postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
                String[] tagNames = postRequest.getTags().split(",");
                for(String name : tagNames) {
                    name = name.trim().toLowerCase();
                    Tag tag = tagRepo.findByName(name);
                    if(tag == null) {
                        tag = new Tag();
                        tag.setName(name);
                        tag = tagRepo.save(tag);
                    }
                    postTags.add(tag);
                }
            }
            post.setTags(postTags);
        }

        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);

        return postRepo.save(post);
    }
}
