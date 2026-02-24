package com.summat.summat.reply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReplyCreateReqDto {

    @NotBlank
    private String content;

    // null이면 댓글(depth=0), 값 있으면 대댓글(depth=1)
    private Long parentId;
}
