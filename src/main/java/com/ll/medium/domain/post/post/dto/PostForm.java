package com.ll.medium.domain.post.post.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {

    @NotEmpty(message = "제목을 입력 해 주세요.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message = "내용을 입력 해 주세요.")
    private String content;

    private boolean isPremium;
    private boolean isPublished;

}
