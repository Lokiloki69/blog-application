package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.model.Tag;
import com.example.BlogApplication.repo.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final TagService tagService;

    @Autowired
    public PostServiceImpl(PostRepo postRepo,TagService tagService) {
        this.postRepo = postRepo;
        this.tagService=tagService;
    }

    @Override
    public void savePost(Post post,String tagString) {

        String[] tagNames = tagString.split(",");
        List<Tag> tagList = new ArrayList<>();

        for (String tagName : tagNames) {
            tagName = tagName.trim();
            Tag tag = tagService.findTagByName(tagName);
            if (tag == null) {
                tag = new Tag(tagName);
                tagService.saveTag(tag);
            }
            tagList.add(tag);
        }
        post.setTags(tagList);

        String content = post.getContent();
        post.setExcerpt(content.length() > 200 ? content.substring(0, 200) : content);
        post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        post.setPublished(true);
        postRepo.save(post);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepo.findById(id).orElse(null);
    }

    @Override
    public List<Post> getAllPosts(Sort sort) {
       return (sort.isUnsorted())? postRepo.findAll() : postRepo.findAll(sort);
    }

    @Override
    public void deletePostById(Long id) {
        postRepo.deleteById(id);

    }

    @Override
    public List<Post> searchPosts(String query, Sort sort) {
        if (query == null || query.trim().isEmpty() || query.isBlank()) {
            return getAllPosts(sort);
        }
        String kw = query.trim();
        kw = "%" + kw.toLowerCase()+"%";

        return postRepo.findBySearchQuery(kw,sort);
    }

    @Override
    public List<Post> getPostsByAuthorAndTags(Long authorId, List<Long> tagIds,Sort sort) {
        boolean hasAuthor = (authorId != null);
        boolean hasTags = (tagIds != null && !tagIds.isEmpty());

        List<Post> result;

        if(hasAuthor && hasTags){
            result = postRepo.findDistinctByUser_IdAndTags_IdIn(authorId, tagIds);
        }
        else if(hasAuthor){
            result = postRepo.findDistinctByUser_Id(authorId);
        } else if(hasTags) {
            result = postRepo.findDistinctByTags_IdIn(tagIds);
        }
        else {
            result = getAllPosts(sort);
        }
        return result;
    }
}
