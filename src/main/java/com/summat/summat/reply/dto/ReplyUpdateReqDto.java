package com.summat.summat.reply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReplyUpdateReqDto {

    @NotBlank
    private String content;
}
