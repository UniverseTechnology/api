package com.coreapi.api;

import lombok.Data;

@Data

public class CreatePostRequest {
    private Long authorId;
    private String content;
}
