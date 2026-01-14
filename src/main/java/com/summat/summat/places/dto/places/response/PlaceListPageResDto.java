package com.summat.summat.places.dto.places.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceListPageResDto {
    List<PlaceMainListResDto> placeList;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}
