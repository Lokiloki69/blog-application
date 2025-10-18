package com.example.BlogApplication.service;

import com.example.BlogApplication.model.Tag;
import java.util.List;

public interface TagService {
    Tag findTagByName(String name);
    void saveTag(Tag tag);
    List<Tag> getAllTags();
    List<Tag> processTags(String tagString);
}
