package com.summat.summat.comments.dto;

import com.summat.summat.comments.entity.Comment;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class CommentResDto {

    private Long id;
    private Long placeId;
    private Long userId;
    private String nickName;
    private String content;
    private int depth;
    private boolean deleted;
    private boolean hidden;
    private Instant createdAt;
    private List<CommentResDto> replies;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.placeId = comment.getPlace().getId();
        this.userId = comment.getUser().getId();
        this.nickName = comment.getUser().getUserNickName();
        this.depth = comment.getDepth();
        this.deleted = comment.isDeleted();
        this.hidden = comment.isHidden();
        this.createdAt = comment.getCreatedAt();

        if (comment.isDeleted()) {
            this.content = "삭제된 댓글입니다.";
        } else if (comment.isHidden()) {
            this.content = "숨김 처리된 댓글입니다.";
        } else {
            this.content = comment.getContent();
        }
    }

    public void setReplies(List<CommentResDto> replies) {
        this.replies = replies;
    }
}
