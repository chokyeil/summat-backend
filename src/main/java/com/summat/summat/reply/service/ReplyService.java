package com.summat.summat.reply.service;

import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.reply.dto.ReplyCreateReqDto;
import com.summat.summat.reply.dto.ReplyResDto;
import com.summat.summat.reply.dto.ReplyUpdateReqDto;
import com.summat.summat.reply.entity.Reply;
import com.summat.summat.reply.repository.ReplyRepository;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PlacesRepository placesRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public ReplyResDto create(Long placeId, Long userId, ReplyCreateReqDto req) {
        Places place = placesRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Reply reply = new Reply();
        reply.setPlace(place);
        reply.setUser(user);
        reply.setContent(req.getContent());

        if (req.getParentId() != null) {
            Reply parent = replyRepository.findById(req.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            if (parent.getDepth() != 0) {
                throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다.");
            }
            reply.setParent(parent);
            reply.setDepth(1);
        } else {
            reply.setDepth(0);
        }

        return new ReplyResDto(replyRepository.save(reply));
    }

    @Transactional(readOnly = true)
    public List<ReplyResDto> getReplies(Long placeId) {
        List<Reply> parents = replyRepository.findByPlace_IdAndDepthOrderByCreatedAtDesc(placeId, 0);
        return parents.stream().map(parent -> {
            ReplyResDto dto = new ReplyResDto(parent);
            List<ReplyResDto> children = replyRepository
                    .findByParent_IdOrderByCreatedAtAsc(parent.getId())
                    .stream()
                    .map(ReplyResDto::new)
                    .collect(Collectors.toList());
            dto.setReplies(children);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public ReplyResDto update(Long replyId, Long userId, ReplyUpdateReqDto req) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!reply.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }
        if (reply.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글입니다.");
        }
        reply.setContent(req.getContent());
        return new ReplyResDto(replyRepository.save(reply));
    }

    @Transactional
    public void delete(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!reply.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }
        reply.setDeleted(true);
    }
}
