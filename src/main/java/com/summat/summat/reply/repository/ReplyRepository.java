package com.summat.summat.reply.repository;

import com.summat.summat.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 장소의 루트 댓글(depth=0), 최신순
    List<Reply> findByPlace_IdAndDepthOrderByCreatedAtDesc(Long placeId, int depth);

    // 특정 댓글의 대댓글, 등록순
    List<Reply> findByParent_IdOrderByCreatedAtAsc(Long parentId);

    // 마이페이지: 내가 쓴 댓글 (최신순)
    List<Reply> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // 관리자용: 전체 최신순
    List<Reply> findAllByOrderByCreatedAtDesc();

    // 장소 삭제 시 연관 댓글 삭제 (자식 먼저 → 부모 순으로 호출)
    @Modifying
    @Query("DELETE FROM Reply r WHERE r.place.id = :placeId AND r.depth = 1")
    void deleteChildRepliesByPlaceId(@Param("placeId") Long placeId);

    @Modifying
    @Query("DELETE FROM Reply r WHERE r.place.id = :placeId AND r.depth = 0")
    void deleteParentRepliesByPlaceId(@Param("placeId") Long placeId);
}
