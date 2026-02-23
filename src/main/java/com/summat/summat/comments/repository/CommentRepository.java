package com.summat.summat.comments.repository;

import com.summat.summat.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 장소의 루트 댓글(depth=0), 최신순
    List<Comment> findByPlace_IdAndDepthOrderByCreatedAtDesc(Long placeId, int depth);

    // 특정 댓글의 대댓글, 등록순
    List<Comment> findByParent_IdOrderByCreatedAtAsc(Long parentId);

    // 관리자용: 전체 댓글 최신순
    List<Comment> findAllByOrderByCreatedAtDesc();
}
