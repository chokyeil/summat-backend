package com.summat.summat.admin.controller;

import com.summat.summat.admin.dto.AdminReplyListResDto;
import com.summat.summat.admin.dto.AdminUserDetailResDto;
import com.summat.summat.admin.dto.AdminUserListResDto;
import com.summat.summat.admin.dto.AdminUserStatusReqDto;
import com.summat.summat.admin.service.AdminService;
import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // 회원 목록
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserListResDto>>> getUserList(Pageable pageable) {
        Page<AdminUserListResDto> result = adminService.getUserList(pageable);
        return ResponseEntity.status(ResponseCode.ADMIN_USER_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.ADMIN_USER_LIST_SUCCESS, result));
    }

    // 회원 상세
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDetailResDto>> getUserDetail(@PathVariable Long userId) {
        AdminUserDetailResDto result = adminService.getUserDetail(userId);
        return ResponseEntity.status(ResponseCode.ADMIN_USER_DETAIL_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.ADMIN_USER_DETAIL_SUCCESS, result));
    }

    // 회원 상태 변경
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(
            @PathVariable Long userId,
            @RequestBody AdminUserStatusReqDto req) {
        adminService.changeUserStatus(userId, req);
        return ResponseEntity.status(ResponseCode.ADMIN_USER_STATUS_CHANGED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.ADMIN_USER_STATUS_CHANGED, null));
    }

    // 댓글 목록
    @GetMapping("/reply")
    public ResponseEntity<ApiResponse<List<AdminReplyListResDto>>> getReplyList() {
        List<AdminReplyListResDto> result = adminService.getReplyList();
        return ResponseEntity.status(ResponseCode.ADMIN_REPLY_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.ADMIN_REPLY_LIST_SUCCESS, result));
    }

    // 댓글 삭제 (soft delete)
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<ApiResponse<Void>> deleteReply(@PathVariable Long replyId) {
        adminService.deleteReply(replyId);
        return ResponseEntity.status(ResponseCode.ADMIN_REPLY_DELETED.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.ADMIN_REPLY_DELETED, null));
    }

    // 댓글 숨김
    @PatchMapping("/reply/{replyId}/hide")
    public ResponseEntity<ApiResponse<Void>> hideReply(@PathVariable Long replyId) {
        adminService.hideReply(replyId);
        return ResponseEntity.status(ResponseCode.ADMIN_REPLY_HIDDEN.getHttpStatus())
                .body(new ApiResponse<>(ResponseCode.ADMIN_REPLY_HIDDEN, null));
    }
}
