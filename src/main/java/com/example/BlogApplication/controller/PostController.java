package com.example.BlogApplication.controller;

import com.example.BlogApplication.model.Comment;
import com.example.BlogApplication.model.Post;
import com.example.BlogApplication.model.Tag;
import com.example.BlogApplication.model.User;
import com.example.BlogApplication.service.CommentService;
import com.example.BlogApplication.service.PostService;
import com.example.BlogApplication.service.TagService;
import com.example.BlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, TagService tagService, UserService userService,CommentService commentService) {
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
        this.commentService= commentService;
    }

//    @GetMapping("/")
//    public String homeRedirect() {
//        return "redirect:/home";
//    }

    @GetMapping("/")
    public String home(@RequestParam(value = "authorId", required = false) Long authorId,
                       @RequestParam(value = "tagId", required = false) List<Long> tagIds,
                       @RequestParam(value = "query", required = false) String query,
                       @RequestParam(value = "sort", required = false) String sortOrder,
                       Model model) {

        model.addAttribute("authors", userService.getAllUsers());
        model.addAttribute("allTags", tagService.getAllTags());

        Sort sort= Sort.unsorted();
        if("old".equalsIgnoreCase(sortOrder)){
            sort=Sort.by(Sort.Direction.ASC,"publishedAt");
        }
        else if("new".equalsIgnoreCase(sortOrder)){
            sort = Sort.by(Sort.Direction.DESC,"publishedAt");
        }
        else if("title".equalsIgnoreCase(sortOrder)){
            sort = Sort.by(Sort.Direction.ASC,"title");
        }

        List<Post> posts;
        if(query != null && !query.trim().isEmpty()){
            posts=postService.searchPosts(query,sort);
            authorId = null;
            tagIds = null;
        } else if(authorId != null || (tagIds != null && !tagIds.isEmpty())) {
            posts = postService.getPostsByAuthorAndTags(authorId,tagIds,sort);
        } else {
            posts = postService.getAllPosts(sort);
        }

        model.addAttribute("posts",posts);
        model.addAttribute("searchQuery",query);
        model.addAttribute("sortOrder",sortOrder);
        model.addAttribute("authorId", authorId);
        model.addAttribute("tagIds", tagIds);

        return "home";
    }


    @GetMapping("/newpost")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("tagString", "");
        return "newPost";
    }

    @PostMapping("/savePost")
    public String savePost(@ModelAttribute("post") Post post,
                           @RequestParam("tagString") String tagString) {
        User user = userService.findById(1L);
        post.setUser(user);
        postService.savePost(post, tagString);
        return "redirect:/home";
    }


    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/home";
        }
        List<Comment> comments =commentService.getCommentsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments",comments);
        model.addAttribute("newComment",new Comment());
        return "viewPost";
    }

    @GetMapping("/editPost/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/home";
        }

        String tagString = post.getTags().stream()
                .map(Tag::getName)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        model.addAttribute("post", post);
        model.addAttribute("tagString", tagString);
        return "newPost";
    }

    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute("post") Post post,
                             @RequestParam("tagString") String tagString) {

        User user = userService.findById(1L);
        post.setUser(user);
        postService.savePost(post, tagString);
        return "redirect:/home";
    }

    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return "redirect:/home";
    }

    @PostMapping("/post/{id}/comment")
    public String addComment(@PathVariable("id") Long postId,
                             @ModelAttribute("newComment") Comment comment) {

        Post post = postService.getPostById(postId);
//        comment.setId(null);
        comment.setPost(post);
        commentService.saveComment(comment);
        return "redirect:/post/" + postId;
    }

}
