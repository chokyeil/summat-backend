package com.summat.summat.users.dto.mypage;

import com.summat.summat.reply.entity.Reply;
import lombok.Getter;

import java.time.Instant;

@Getter
public class MyReplyResDto {
    private Long replyId;
    private Long placeId;
    private String placeName;
    private String content;
    private int depth;
    private boolean deleted;
    private boolean hidden;
    private Instant createdAt;

    public MyReplyResDto(Reply reply) {
        this.replyId = reply.getId();
        this.placeId = reply.getPlace().getId();
        this.placeName = reply.getPlace().getName();
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
}
