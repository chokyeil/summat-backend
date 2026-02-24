package com.summat.summat.reply.dto;

import com.summat.summat.reply.entity.Reply;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class ReplyResDto {

    private Long id;
    private Long placeId;
    private Long userId;
    private String nickName;
    private String content;
    private int depth;
    private boolean deleted;
    private boolean hidden;
    private Instant createdAt;
    private List<ReplyResDto> replies;

    public ReplyResDto(Reply reply) {
        this.id = reply.getId();
        this.placeId = reply.getPlace().getId();
        this.userId = reply.getUser().getId();
        this.nickName = reply.getUser().getUserNickName();
        this.depth = reply.getDepth();
        this.deleted = reply.isDeleted();
        this.hidden = reply.isHidden();
        this.createdAt = reply.getCreatedAt();

        if (reply.isDeleted()) {
            this.content = "삭제된 댓글입니다.";
        } else if (reply.isHidden()) {
            this.content = "숨김 처리된 댓글입니다.";
        } else {
            this.content = reply.getContent();
        }
    }

    public void setReplies(List<ReplyResDto> replies) {
        this.replies = replies;
    }
}
