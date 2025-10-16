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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

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


    @GetMapping("/")
    public String home(@RequestParam(value = "authorId", required = false) List<Long> authorIds,
                       @RequestParam(value = "tagId", required = false) List<Long> tagIds,
                       @RequestParam(value = "query", required = false) String query,
                       @RequestParam(value = "sort", required = false) String sortOrder,
                       @RequestParam(value = "start", defaultValue = "1") int start,
                       @RequestParam(value = "limit", defaultValue = "10") int limit,
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

        int pageNumber = (start -1)/limit;
        Pageable pageable = PageRequest.of(pageNumber,limit,sort);
        Page<Post> postsPage;

        if(query != null && !query.trim().isEmpty()){
            postsPage =postService.searchPosts(query,pageable);
            authorIds = null;
            tagIds = null;
        } else if((authorIds != null && !authorIds.isEmpty()) || (tagIds != null && !tagIds.isEmpty())) {
            postsPage = postService.getPostsByAuthorAndTags(authorIds,tagIds,pageable);
        } else {
            postsPage = postService.getAllPosts(pageable);
        }

        model.addAttribute("posts",postsPage.getContent());
        model.addAttribute("currentPage",pageNumber+1);
        model.addAttribute("totalPages",postsPage.getTotalPages());
        model.addAttribute("limit",limit);
        model.addAttribute("start",start);
        model.addAttribute("searchQuery",query);
        model.addAttribute("sortOrder",sortOrder);
        model.addAttribute("authorIds", authorIds);
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User user = userService.findByEmail(auth.getName());
            post.setUser(user);
        } else {
            User user = userService.findById(1L);
            post.setUser(user);
        }

        postService.savePost(post, tagString);
        return "redirect:/";
    }


    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/";
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
            return "redirect:/";
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User user = userService.findByEmail(auth.getName());
            post.setUser(user);
        } else {
            User user = userService.findById(1L);
            post.setUser(user);
        }
        postService.savePost(post, tagString);
        return "redirect:/";
    }

    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return "redirect:/";
    }


}
