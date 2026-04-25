package com.coreapi.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;


    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request){
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request){
        return ResponseEntity.ok(postService.addComment(postId, request));
    }

    @PostMapping("/{postId}/Like")
    public ResponseEntity<String> likePost(@PathVariable Long postId){
        postService.likePost(postId);
        return ResponseEntity.ok("Liked");
    }

}
