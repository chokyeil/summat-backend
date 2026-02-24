package com.summat.summat.reply.controller;

import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.reply.dto.ReplyCreateReqDto;
import com.summat.summat.reply.dto.ReplyResDto;
import com.summat.summat.reply.dto.ReplyUpdateReqDto;
import com.summat.summat.reply.service.ReplyService;
import com.summat.summat.users.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;

    // 댓글/대댓글 작성
    @PostMapping("/{placeId}")
    public ResponseEntity<ApiResponse> create(
            @PathVariable Long placeId,
            @RequestBody @Valid ReplyCreateReqDto req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReplyResDto result = replyService.create(placeId, userDetails.getUser().getId(), req);
        return ResponseEntity.status(ResponseCode.REPLY_CREATED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.REPLY_CREATED, result));
    }

    // 댓글 목록 조회 (루트 댓글 createdAt DESC, 대댓글 포함)
    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse> getList(@PathVariable Long placeId) {
        List<ReplyResDto> result = replyService.getReplies(placeId);
        return ResponseEntity.status(ResponseCode.REPLY_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.REPLY_LIST_SUCCESS, result));
    }

    // 댓글 수정
    @PutMapping("/{replyId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long replyId,
            @RequestBody @Valid ReplyUpdateReqDto req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReplyResDto result = replyService.update(replyId, userDetails.getUser().getId(), req);
        return ResponseEntity.status(ResponseCode.REPLY_UPDATED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.REPLY_UPDATED, result));
    }

    // 댓글 소프트 삭제
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long replyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        replyService.delete(replyId, userDetails.getUser().getId());
        return ResponseEntity.status(ResponseCode.REPLY_DELETED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.REPLY_DELETED, null));
    }
}
