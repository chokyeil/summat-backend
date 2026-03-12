package com.summat.summat.places.controller;

import com.summat.summat.common.response.ApiResponse;
import com.summat.summat.common.response.ResponseCode;
import com.summat.summat.places.dto.places.response.PlaceLikeResDto;
import com.summat.summat.places.dto.places.response.PlaceListPageResDto;
import com.summat.summat.places.dto.places.response.PlacesDetailResDto;
import com.summat.summat.places.dto.places.response.PlaceViewResDto;
import com.summat.summat.places.dto.places.request.PlacesReqDto;
import com.summat.summat.places.service.PlacesService;
import com.summat.summat.users.CustomUserDetails;
import com.summat.summat.users.repository.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        log.info("placesReqDto.getLotAddress() = " + placesReqDto.getLotAddress());

        boolean isCreated = placesService.createdPlace(placesReqDto, image, userId);

        return ResponseEntity.status(isCreated ? ResponseCode.PLACE_CREATED.getHttpStatus() : ResponseCode.PLACE_CREATE_FAILED.getHttpStatus())
                             .body(isCreated ? new ApiResponse(ResponseCode.PLACE_CREATED, null) : new ApiResponse(ResponseCode.PLACE_CREATE_FAILED, null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> boardList(Pageable pageable,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        PlaceListPageResDto resultData = placesService.getPlacesList(pageable, userId);

        return ResponseEntity.status(ResponseCode.PLACE_LIST_SUCCESS.getHttpStatus())
                             .body(new ApiResponse(ResponseCode.PLACE_LIST_SUCCESS, resultData));
    }

    @PutMapping(value = "/update/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updatePlace(@ModelAttribute @Valid PlacesReqDto placesReqDto,
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
    public ResponseEntity<ApiResponse> detailPlace(@PathVariable(name = "placeId") Long placeId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        PlacesDetailResDto detailPlaceResult = placesService.detailPlace(placeId, userId);

        return ResponseEntity.status(detailPlaceResult != null ? ResponseCode.PLACE_DETAIL_SUCCESS.getHttpStatus() : ResponseCode.PLACE_NOT_FOUND.getHttpStatus())
                .body(detailPlaceResult != null ? new ApiResponse(ResponseCode.PLACE_DETAIL_SUCCESS, detailPlaceResult) : new ApiResponse(ResponseCode.PLACE_NOT_FOUND, null));
    }

    @PostMapping("/view/{placeId}")
    public ResponseEntity<ApiResponse> increaseView(@PathVariable(name = "placeId") Long placeId) {
        PlaceViewResDto result = placesService.increaseView(placeId);
        return ResponseEntity.status(ResponseCode.PLACE_VIEW_INCREMENTED.getHttpStatus())
                .body(new ApiResponse(ResponseCode.PLACE_VIEW_INCREMENTED, result));
    }

    @PostMapping("/like/{placeId}")
    public ResponseEntity<ApiResponse> toggleLike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable(name = "placeId") Long placeId) {
        Long userId = userDetails.getUser().getId();

        PlaceLikeResDto result = placesService.toggleLike(userId, placeId);
        ResponseCode code = result.isLiked() ? ResponseCode.PLACE_LIKE_SUCCESS : ResponseCode.PLACE_LIKE_CANCELLED;

        return ResponseEntity.status(code.getHttpStatus())
                .body(new ApiResponse(code, result));
    }

    // 복합 조건 조회
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchSummatList(Pageable pageable,
                                                        @RequestParam(name = "q", required = false) String query,
                                                        @RequestParam(name = "categories", required = false) List<String> categories,
                                                        @RequestParam(name = "regions", required = false) List<String> regions,
                                                        @RequestParam(name = "tags", required = false) List<String> tags,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("q='{}'", query);
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;

        PlaceListPageResDto searchResult = placesService.searchSummatList(pageable, query, categories, regions, tags, userId);



        return ResponseEntity.status(ResponseCode.PLACE_LIST_SUCCESS.getHttpStatus())
                .body(new ApiResponse(ResponseCode.PLACE_LIST_SUCCESS, searchResult));
    }


}
