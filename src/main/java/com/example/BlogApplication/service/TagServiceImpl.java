package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Tag;
import com.example.BlogApplication.repo.TagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepo tagRepo;

    @Autowired
    public TagServiceImpl(TagRepo tagRepo) {
        this.tagRepo = tagRepo;
    }

    @Override
    public Tag findTagByName(String name) {
        return tagRepo.findByName(name).orElse(null);
    }

    @Override
    public void saveTag(Tag tag) {
        tagRepo.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepo.findAll();
    }
}
