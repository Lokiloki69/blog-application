package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Tag;
import com.example.BlogApplication.repo.TagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<Tag> processTags(String tagString) {
        if (tagString == null || tagString.trim().isEmpty()) return new ArrayList<>();

        String[] tagNames = tagString.split(",");
        List<Tag> tags = new ArrayList<>();

        for (String name : tagNames) {
            Tag tag = tagRepo.findByNameIgnoreCase(name.trim());
            if (tag == null) {
                tag = new Tag();
                tag.setName(name.trim());
                tagRepo.save(tag);
            }
            tags.add(tag);
        }
        return tags;
    }

}
