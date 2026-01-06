package com.summat.summat.places.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.summat.summat.places.dto.places.PlaceMainListResDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PlaceQueryRepositoryImpl implements PlaceQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public PlaceMainListResDto findMainList() {
        return null;
    }
}
