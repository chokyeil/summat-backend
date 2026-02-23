package com.summat.summat.comments.controller;

import com.summat.summat.comments.dto.CommentCreateReqDto;
import com.summat.summat.comments.dto.CommentResDto;
import com.summat.summat.comments.dto.CommentUpdateReqDto;
import com.summat.summat.comments.service.CommentService;
import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.users.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글/대댓글 작성
    @PostMapping("/{placeId}")
    public ResponseEntity<ApiResponse> create(
            @PathVariable Long placeId,
            @RequestBody @Valid CommentCreateReqDto req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResDto result = commentService.create(placeId, userDetails.getUser().getId(), req);
        return ResponseEntity.status(ResponseCode.COMMENT_CREATED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.COMMENT_CREATED, result));
    }

    // 댓글 목록 조회 (루트 댓글 createdAt DESC, 대댓글 포함)
    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse> getList(@PathVariable Long placeId) {
        List<CommentResDto> result = commentService.getComments(placeId);
        return ResponseEntity.status(ResponseCode.COMMENT_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.COMMENT_LIST_SUCCESS, result));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResDto>> update(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateReqDto req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResDto result = commentService.update(commentId, userDetails.getUser().getId(), req);
        return ResponseEntity.status(ResponseCode.COMMENT_UPDATED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.COMMENT_UPDATED, result));
    }

    // 댓글 소프트 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(commentId, userDetails.getUser().getId());
        return ResponseEntity.status(ResponseCode.COMMENT_DELETED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.COMMENT_DELETED, null));
    }
}
