package com.summat.summat.admin.service;

import com.summat.summat.admin.dto.AdminReplyListResDto;
import com.summat.summat.admin.dto.AdminUserDetailResDto;
import com.summat.summat.admin.dto.AdminUserListResDto;
import com.summat.summat.admin.dto.AdminUserStatusReqDto;
import com.summat.summat.reply.entity.Reply;
import com.summat.summat.reply.repository.ReplyRepository;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsersRepository usersRepository;
    private final ReplyRepository replyRepository;

    // 회원 목록 (페이징)
    @Transactional(readOnly = true)
    public Page<AdminUserListResDto> getUserList(Pageable pageable) {
        return usersRepository.findAll(pageable).map(AdminUserListResDto::new);
    }

    // 회원 상세
    @Transactional(readOnly = true)
    public AdminUserDetailResDto getUserDetail(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return new AdminUserDetailResDto(user);
    }

    // 회원 상태 변경 (ACTIVE / SUSPENDED / BANNED)
    @Transactional
    public void changeUserStatus(Long userId, AdminUserStatusReqDto req) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        user.setStatus(req.getStatus());
    }

    // 댓글 전체 목록
    @Transactional(readOnly = true)
    public List<AdminReplyListResDto> getReplyList() {
        return replyRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdminReplyListResDto::new)
                .collect(Collectors.toList());
    }

    // 댓글 soft delete
    @Transactional
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        reply.setDeleted(true);
    }

    // 댓글 숨김
    @Transactional
    public void hideReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        reply.setHidden(true);
    }
}
