package com.summat.summat.places.controller;

import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.places.dto.places.PlaceListPageResDto;
import com.summat.summat.places.dto.places.PlaceMainListResDto;
import com.summat.summat.places.dto.places.PlacesDetailResDto;
import com.summat.summat.places.dto.places.PlacesReqDto;
import com.summat.summat.places.entity.PlaceLike;
import com.summat.summat.places.entity.Places;
import com.summat.summat.places.service.PlacesService;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.repository.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse> createdPlace(@ModelAttribute @Valid PlacesReqDto placesReqDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info(">>> /places/add controller 진입!!!");

        Long userId = userDetails.getUser().getId();


        log.info("placesReqDto.getPlaceName() = " + placesReqDto.getPlaceName());
        log.info("placesReqDto.getPlaceLotAddress() = " + placesReqDto.getPlaceLotAddress());

        boolean isCreated = placesService.createdPlace(placesReqDto, image, userId);

        return ResponseEntity.status(isCreated ? ResponseCode.PLACE_CREATED.getHttpStatus() : ResponseCode.PLACE_CREATE_FAILED.getHttpStatus())
                             .body(isCreated ? new ApiResponse(ResponseCode.PLACE_CREATED, null) : new ApiResponse(ResponseCode.PLACE_CREATE_FAILED, null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> boardList(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {



        PlaceListPageResDto resultData = placesService.getPlacesList(page, size);


        return ResponseEntity.status(ResponseCode.PLACE_LIST_SUCCESS.getHttpStatus())
                             .body(new ApiResponse(ResponseCode.PLACE_LIST_SUCCESS, resultData));
    }

    @PutMapping("/update/{placeId}")
    public ResponseEntity<ApiResponse> updatePlace(@RequestBody PlacesReqDto placesReqDto,
                                               @AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestPart(value = "image", required = false) MultipartFile image,
                                               @PathVariable(name = "placeId") Long placeId) {
        Long userId = userDetails.getUser().getId();

        boolean isUpdate = placesService.updatePlace(placesReqDto, image, userId, placeId);

        return ResponseEntity.status(isUpdate ? ResponseCode.PLACE_UPDATED.getHttpStatus() : ResponseCode.PLACE_NOT_FOUND.getHttpStatus())
                .body(isUpdate ? new ApiResponse(ResponseCode.PLACE_UPDATED, null) : new ApiResponse(ResponseCode.PLACE_NOT_FOUND, null));
    }

    @DeleteMapping("/remove/{placeId}")
    public ResponseEntity<ApiResponse> removePlace(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable(name = "placeId") Long placeId) {
        Long userId = userDetails.getUser().getId();

        boolean isRemove = placesService.removePlace(userId, placeId);


        return ResponseEntity.status(isRemove ? ResponseCode.PLACE_DELETED.getHttpStatus() : ResponseCode.PLACE_NOT_FOUND.getHttpStatus())
                .body(isRemove ? new ApiResponse(ResponseCode.PLACE_DELETED, null) : new ApiResponse(ResponseCode.PLACE_NOT_FOUND, null));
    }

    @GetMapping("/detail/{placeId}")
    public ResponseEntity<ApiResponse> detailPlace(@PathVariable(name = "placeId") Long placeId) {


        PlacesDetailResDto detailPlaceResult = placesService.detailPlace(placeId);

        return ResponseEntity.status(detailPlaceResult != null ? ResponseCode.PLACE_DETAIL_SUCCESS.getHttpStatus() : ResponseCode.PLACE_NOT_FOUND.getHttpStatus())
                .body(detailPlaceResult != null ? new ApiResponse(ResponseCode.PLACE_DETAIL_SUCCESS, null) : new ApiResponse(ResponseCode.PLACE_NOT_FOUND, null));
    }

    @PostMapping("/view/{placeId}")
    public ResponseEntity<ApiResponse> increaseView(@PathVariable(name = "placeId") Long placeId) {

        boolean isCreaseView = placesService.increaseView(placeId);

        return ResponseEntity.status(isCreaseView ? ResponseCode.PLACE_VIEW_INCREMENTED.getHttpStatus() : ResponseCode.PLACE_NOT_FOUND.getHttpStatus())
                .body(isCreaseView ? new ApiResponse(ResponseCode.PLACE_DETAIL_SUCCESS, null) : new ApiResponse(ResponseCode.PLACE_NOT_FOUND, null));
    }

    @PostMapping("/like/{placeId}")
    public ResponseEntity<ApiResponse> toggleLike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable(name = "placeId") Long placeId) {
        Long userId = userDetails.getUser().getId();

        boolean isPlaceLike = placesService.toggleLike(userId, placeId);

        return ResponseEntity.status(isPlaceLike ? ResponseCode.PLACE_LIKE_SUCCESS.getHttpStatus() : ResponseCode.PLACE_NOT_FOUND.getHttpStatus())
                .body(isPlaceLike ? new ApiResponse(ResponseCode.PLACE_LIKE_SUCCESS, null) : new ApiResponse(ResponseCode.PLACE_NOT_FOUND, null));
    }

    // 복합 조건 조회
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchSummatList(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(name = "q", required = false) String query,
                                                        @RequestParam(name = "region", required = false) String region,
                                                        @RequestParam(name = "type", required = false) String type,
                                                        @RequestParam(name = "tags", required = false) List<String> tags) {


        PlaceListPageResDto searchResult = placesService.searchSummatList(page, size, query, region, type, tags);



        return ResponseEntity.status(ResponseCode.PLACE_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse(ResponseCode.PLACE_LIST_SUCCESS, searchResult));
    }


}
