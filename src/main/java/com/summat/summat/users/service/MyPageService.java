package com.summat.summat.users.service;

import com.summat.summat.places.entity.PlaceLike;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlaceLikeRepository;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.reply.entity.Reply;
import com.summat.summat.reply.repository.ReplyRepository;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.mypage.MyLikedPlaceResDto;
import com.summat.summat.users.dto.mypage.MyPlaceResDto;
import com.summat.summat.users.dto.mypage.MyReplyResDto;
import com.summat.summat.users.dto.mypage.PasswordChangeReqDto;
import com.summat.summat.users.dto.mypage.ProfileResDto;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlacesRepository placesRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final ReplyRepository replyRepository;

    public ProfileResDto getMyProfile(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        ProfileResDto profileResDto = new ProfileResDto();
        profileResDto.setId(user.getId());
        profileResDto.setUserId(user.getEmail());
        profileResDto.setNickName(user.getUserNickName());
        profileResDto.setCreatedAt(user.getCreatedAt());

        return profileResDto;

    }

    public List<MyPlaceResDto> getMyPlaces(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<Places> places = placesRepository.findByCreatedBy_IdOrderByCreatedAtDesc(userId);

        List<MyPlaceResDto> result = new ArrayList<>();
        for (Places place : places) {
            MyPlaceResDto dto = new MyPlaceResDto();
            dto.setPlaceId(place.getId());
            dto.setPlaceName(place.getName());
            dto.setImageUrl(place.getImageUrl());
            dto.setSummary(place.getSummary());
            dto.setCategory(place.getCategory());
            dto.setRegion(place.getRegion());
            dto.setCreatedAt(place.getCreatedAt());
            result.add(dto);
        }
        return result;
    }

    public boolean changePassword(CustomUserDetails userDetails, PasswordChangeReqDto passwordChangeReqDto) {
        Long userId = userDetails.getUser().getId();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        String newPassword = passwordChangeReqDto.getNewPassword();
        String getNewPasswordConfirm = passwordChangeReqDto.getNewPasswordConfirm();

        if(newPassword.equals(getNewPasswordConfirm)) {

            user.setUserPw(passwordEncoder.encode(newPassword));
            usersRepository.save(user);

            return true;
        } else {
            return false;
        }


    }

    public List<MyReplyResDto> getMyReplies(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<Reply> replies = replyRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        List<MyReplyResDto> result = new ArrayList<>();
        for (Reply reply : replies) {
            result.add(new MyReplyResDto(reply));
        }
        return result;
    }

    public List<MyLikedPlaceResDto> getLikedPlaces(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<PlaceLike> likes = placeLikeRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Long> placeIds = likes.stream().map(PlaceLike::getPlaceId).collect(Collectors.toList());
        Map<Long, Places> placeMap = placesRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(Places::getId, p -> p));

        List<MyLikedPlaceResDto> result = new ArrayList<>();
        for (PlaceLike like : likes) {
            Places place = placeMap.get(like.getPlaceId());
            if (place == null) continue;

            MyLikedPlaceResDto dto = new MyLikedPlaceResDto();
            dto.setPlaceId(place.getId());
            dto.setPlaceName(place.getName());
            dto.setImageUrl(place.getImageUrl());
            dto.setSummary(place.getSummary());
            dto.setCategory(place.getCategory());
            dto.setRegion(place.getRegion());
            dto.setLiked(true);
            dto.setLikeCount(place.getLikeCount());
            dto.setCreatedAt(place.getCreatedAt());
            result.add(dto);
        }
        return result;
    }
}
