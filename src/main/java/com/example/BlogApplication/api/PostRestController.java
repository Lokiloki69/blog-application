package com.example.BlogApplication.api;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PostRestController{

    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public PostRestController(PostService postService, TagService tagService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
        this.commentService= commentService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        return ResponseEntity.ok(Map.of(
                "message", "API is working",
                "status", "success",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(value = "authorId",required = false) List<Long> authorIds,
            @RequestParam(value = "tagId",required = false) List<Long> tagIds,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "sort",required = false) String sortOrder,
            @RequestParam(value = "start", defaultValue = "1") int start,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ){
        Sort sort = Sort.unsorted();
        if("old".equalsIgnoreCase(sortOrder)){
            sort= Sort.by(Sort.Direction.ASC,"publishedAt");
        } else if("new ".equalsIgnoreCase(sortOrder)) {
            sort =Sort.by(Sort.Direction.DESC,"publishedAt");
        } else if("title".equalsIgnoreCase(sortOrder)) {
            sort=Sort.by(Sort.Direction.ASC,"title");
        }

        int pageNumber = (start -1)/limit;
        Pageable pageable = PageRequest.of(pageNumber,limit,sort);
        Page<Post> postsPage;

        if (query != null && !query.trim().isEmpty()) {
            postsPage = postService.searchPosts(query, pageable);
            authorIds = null;
            tagIds = null;
        } else if ((authorIds != null && !authorIds.isEmpty()) || (tagIds != null && !tagIds.isEmpty())) {
            postsPage = postService.getPostsByAuthorAndTags(authorIds, tagIds, pageable);
        } else {
            postsPage = postService.getAllPosts(pageable);
        }

        Map<String,Object> response = new LinkedHashMap<>();
        response.put("status","success");
        response.put("totalPages",postsPage.getTotalPages());
        response.put("currentPage",pageNumber+1);
        response.put("limit",limit);
        response.put("start", start);
        response.put("sortOrder", sortOrder);
        response.put("searchQuery", query);
        response.put("authorIds", authorIds);
        response.put("tagIds", tagIds);
        response.put("posts", postsPage.getContent());
        response.put("authors", userService.getAllUsers());
        response.put("tags", tagService.getAllTags());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/post/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Post not found", "id", id));
        }

        List<Comment> comments = commentService.getCommentsByPostId(id);

        // Build JSON structure
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("post", post);
        response.put("comments", comments);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rawPosts")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> postPage = postService.getAllPosts(pageable);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("posts", postPage.getContent());
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> requestBody) {
        try {
            String tagString = (String) requestBody.get("tagString");
            Map<String, Object> postData = (Map<String, Object>) requestBody.get("post");

            Post post = new Post();
            post.setTitle((String) postData.get("title"));
            post.setExcerpt((String) postData.get("excerpt"));
            post.setContent((String) postData.get("content"));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user;
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                user = userService.findByEmail(auth.getName());
            } else {
                user = userService.findById(1L);
            }
            post.setUser(user);

            postService.savePost(post, tagString);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", "success",
                            "message", "Post created successfully",
                            "postId", post.getId()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to create post",
                            "error", e.getMessage()
                    ));
        }
    }

    @PutMapping("/post/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @postService.isOwner(#id,authentication.name)")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Map<String,Object> requestBody){
        Post existing = postService.getPostById(id);
        if(existing == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Post not found","id",id));
        }

        try{
            String tagString = (String) requestBody.get("tagString");
            Map<String, Object> postData = (Map<String,Object>) requestBody.get("post");

            existing.setTitle((String)postData.get("title"));
            existing.setExcerpt((String)postData.get("excerpt"));
            existing.setContent((String) postData.get("content"));

            postService.savePost(existing,tagString);

            return ResponseEntity.ok(Map.of(
                    "status","success",
                    "message","Post updated successfully"
            ));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status","error",
                            "message","Failed to update post",
                            "error",e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/post/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @postService.isOwner(#id,authentication.name)")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
        Post post = postService.getPostById(id);
        if(post == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Post not found","id",id));
        }
        try{
            postService.deletePostById(id);
            return ResponseEntity.ok(Map.of(
                    "status","success",
                    "message","Post deleted successfully"
            ));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status","error",
                            "message","Failed to deleted Post",
                            "error",e.getMessage()
                    ));
        }
    }



}
