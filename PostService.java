package com.coreapi.api;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public Post createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Comment addComment(Long postId, CreateCommentRequest request) {
        if (request.getDepthLevel() > 20) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Depth level is high");
        }
        if (request.isBot()) {
            String botCountKey = "post:" + postId + ":bot_count";
            Long botCount = redisTemplate.opsForValue().increment(botCountKey);
            if (botCount >= 100) {
                redisTemplate.opsForValue().decrement(botCountKey);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Limit Reached");
            }
            String coolDownKey = "cooldown:bot_" + request.getAuthorId() + ":human_" + postId;
            Boolean exists = redisTemplate.hasKey(coolDownKey);
            if (Boolean.TRUE.equals(exists)) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bot on cooldown");
            }
            Comment comment= new Comment();
            Comment saved=commentRepository.save(comment);
            redisTemplate.opsForValue().set(coolDownKey, "1", 10, TimeUnit.MINUTES);
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(request.getAuthorId());
        comment.setContent(request.getContent());
        comment.setDepthLevel(request.getDepthLevel());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);

    }
    public void likePost(Long postId){
        String key ="post:"+postId+":virality_score_point";
        redisTemplate.opsForValue().increment(key, 20);
    }


}
