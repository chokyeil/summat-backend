package com.summat.summat.admin.dto;

import com.summat.summat.comments.entity.Comment;
import lombok.Getter;

import java.time.Instant;

@Getter
public class AdminCommentListResDto {

    private Long id;
    private Long placeId;
    private Long userId;
    private String userNickName;
    private String content;
    private int depth;
    private boolean deleted;
    private boolean hidden;
    private Instant createdAt;

    public AdminCommentListResDto(Comment comment) {
        this.id = comment.getId();
        this.placeId = comment.getPlace().getId();
        this.userId = comment.getUser().getId();
        this.userNickName = comment.getUser().getUserNickName();
        this.content = comment.getContent();
        this.depth = comment.getDepth();
        this.deleted = comment.isDeleted();
        this.hidden = comment.isHidden();
        this.createdAt = comment.getCreatedAt();
    }
}
