package com.summat.summat.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentCreateReqDto {

    @NotBlank
    private String content;

    // null이면 댓글, 값 있으면 대댓글
    private Long parentId;
}
