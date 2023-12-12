package com.ll.medium.domain.post.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message = "답변을 입력 해 주세요.")
    private String content;
}
