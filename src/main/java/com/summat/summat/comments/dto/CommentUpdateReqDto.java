package com.summat.summat.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateReqDto {

    @NotBlank
    private String content;
}
