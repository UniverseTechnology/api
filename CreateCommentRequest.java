package com.coreapi.api;

import lombok.Data;

@Data

public class CreateCommentRequest {
    private Long authorId;
    private String content;
    private int depthLevel;
    private boolean isBot;
}
