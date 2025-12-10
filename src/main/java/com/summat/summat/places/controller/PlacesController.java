package com.summat.summat.places.controller;

import com.summat.summat.places.dto.PlacesDetailResDto;
import com.summat.summat.places.dto.PlacesReqDto;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.service.PlacesService;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.entity.Users;
import com.summat.summat.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Slf4j
public class PlacesController {
    private final PlacesService placesService;
    private final UsersRepository usersRepository;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HashMap<String, Object> createdPlace(@ModelAttribute PlacesReqDto placesReqDto,
                                                @RequestPart(value = "image", required = false) MultipartFile image,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info(">>> /places/add controller 진입!!!");

            Long userId = userDetails.getUser().getId();
            HashMap<String, Object> result = new HashMap<>();

            Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        log.info("placesReqDto.getPlaceName() = " + placesReqDto.getPlaceName());
        log.info("placesReqDto.getPlaceDetailAddress() = " + placesReqDto.getPlaceDetailAddress());

            boolean isCreated = placesService.createdPlace(placesReqDto, image, user);

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
        result.put("data", resultData);

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

    @GetMapping("/detail/{placeId}")
    public HashMap<String, Object> detailPlace(@PathVariable(name = "placeId") Long placeId) {

        HashMap<String, Object> result = new HashMap<>();

        PlacesDetailResDto detailPlaceResult = placesService.detailPlace(placeId);

        result.put("status", detailPlaceResult != null ? 200 : 500);
        result.put("message", detailPlaceResult != null ? "detail place sucess" : "detail place fail");
        result.put("data", detailPlaceResult);

        return result;
    }
}
