package com.summat.summat.reply.repository;

import com.summat.summat.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 장소의 루트 댓글(depth=0), 최신순
    List<Reply> findByPlace_IdAndDepthOrderByCreatedAtDesc(Long placeId, int depth);

    // 특정 댓글의 대댓글, 등록순
    List<Reply> findByParent_IdOrderByCreatedAtAsc(Long parentId);

    // 관리자용: 전체 최신순
    List<Reply> findAllByOrderByCreatedAtDesc();
}
