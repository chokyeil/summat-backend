package com.summat.summat.places.repository;

import com.summat.summat.places.dto.places.response.PlacesFindResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaceQueryRepository {
    Page<PlacesFindResponseDto> findMainList(String q, List<String> regions, List<String> types, Pageable pageable);
}
