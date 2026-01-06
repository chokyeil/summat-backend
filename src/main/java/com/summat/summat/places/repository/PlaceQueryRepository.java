package com.summat.summat.places.repository;

import com.summat.summat.places.dto.places.PlaceMainListResDto;

public interface PlaceQueryRepository {
    PlaceMainListResDto findMainList();
}
