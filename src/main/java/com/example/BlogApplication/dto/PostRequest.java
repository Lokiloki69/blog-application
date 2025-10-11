package com.example.BlogApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest{
    private String title;
    private String content;
    private Long authorId;
    private String tags;
}
