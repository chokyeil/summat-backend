package com.summat.summat.users.service;

import com.summat.summat.enums.RoleType;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.repository.PlacesRepository;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.dto.mypage.MyPlaceResDto;
import com.summat.summat.users.dto.mypage.PasswordChangeReqDto;
import com.summat.summat.users.dto.mypage.ProfileResDto;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlacesRepository placesRepository;

    public ProfileResDto getMyProfile(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        ProfileResDto profileResDto = new ProfileResDto();
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

            Users userChange = new Users();
            userChange.setEmail(user.getEmail());
            userChange.setUserPw(passwordEncoder.encode(newPassword));
            userChange.setUserNickName(user.getUserNickName());
            userChange.setRole(RoleType.ROLE_USER);
            usersRepository.save(userChange);

            return true;
        } else {
            return false;
        }


    }
}
