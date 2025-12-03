package com.summat.summat.places.controller;

import com.summat.summat.places.dto.PlacesReqDto;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.service.PlacesService;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlacesController {
    private final PlacesService placesService;
    private final UsersRepository usersRepository;

    @PostMapping("/add")
    public HashMap<String, Object> createdPlace(@RequestBody PlacesReqDto placesReqDto,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
            Long userId = userDetails.getUser().getId();
            HashMap<String, Object> result = new HashMap<>();

            Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

            boolean isCreated = placesService.createdPlace(placesReqDto, user);

            result.put("status", isCreated ? 200 : 500);
            result.put("message", isCreated ? "sucess place create" : "fail place create");

        return result;
    }

    @GetMapping("/list")
    public HashMap<String, Object> boardList() {

        HashMap<String, Object> result = new HashMap<>();

        List<Places> resultData = placesService.getPlacesList();

        result.put("status", resultData != null ? 200 : 500);
        result.put("message", resultData != null ? "sucess place list" : "fail place list");
        result.put("data", resultData != null ? resultData : null);

        return result;
    }

    @PutMapping("/update/{placeId}")
    public HashMap<String, Object> updatePlace(@RequestBody PlacesReqDto placesReqDto,
                                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable(name = "placeId") Long placeId) {
        Long userId = userDetails.getUser().getId();
        HashMap<String, Object> result = new HashMap<>();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        boolean isUpdate = placesService.updatePlace(placesReqDto, user, placeId);

        result.put("status", isUpdate ? 200 : 500);
        result.put("message", isUpdate ? "sucess place update" : "fail place update");

        return result;
    }

    @DeleteMapping("/remove/{placeId}")
    public HashMap<String, Object> removePlace(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable(name = "placeId") Long placeId) {
        Long userId = userDetails.getUser().getId();
        HashMap<String, Object> result = new HashMap<>();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        boolean isRemove = placesService.removePlace(placeId);

        result.put("status", isRemove ? 200 : 500);
        result.put("message", isRemove ? "sucess place remove" : "fail place remove");

        return result;
    }
}
