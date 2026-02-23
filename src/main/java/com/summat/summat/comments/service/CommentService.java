package com.summat.summat.comments.service;

import com.summat.summat.comments.dto.CommentCreateReqDto;
import com.summat.summat.comments.dto.CommentResDto;
import com.summat.summat.comments.dto.CommentUpdateReqDto;
import com.summat.summat.comments.entity.Comment;
import com.summat.summat.comments.repository.CommentRepository;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PlacesRepository placesRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public CommentResDto create(Long placeId, Long userId, CommentCreateReqDto req) {
        Places place = placesRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setPlace(place);
        comment.setUser(user);
        comment.setContent(req.getContent());

        if (req.getParentId() != null) {
            Comment parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            if (parent.getDepth() != 0) {
                throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다.");
            }
            comment.setParent(parent);
            comment.setDepth(1);
        } else {
            comment.setDepth(0);
        }

        return new CommentResDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResDto> getComments(Long placeId) {
        List<Comment> parents = commentRepository.findByPlace_IdAndDepthOrderByCreatedAtDesc(placeId, 0);
        return parents.stream().map(parent -> {
            CommentResDto dto = new CommentResDto(parent);
            List<CommentResDto> replies = commentRepository
                    .findByParent_IdOrderByCreatedAtAsc(parent.getId())
                    .stream()
                    .map(CommentResDto::new)
                    .collect(Collectors.toList());
            dto.setReplies(replies);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public CommentResDto update(Long commentId, Long userId, CommentUpdateReqDto req) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }
        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글입니다.");
        }
        comment.setContent(req.getContent());
        return new CommentResDto(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }
        comment.setDeleted(true);
    }
}
