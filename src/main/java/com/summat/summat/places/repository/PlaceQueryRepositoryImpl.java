package com.summat.summat.places.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.summat.summat.places.dto.places.response.PlacesFindResponseDto;
import com.summat.summat.places.entity.QPlaces;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
@AllArgsConstructor
public class PlaceQueryRepositoryImpl implements PlaceQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PlacesFindResponseDto> findMainList(String q, List<String> regions, List<String> types, Pageable pageable) {
        QPlaces p = QPlaces.places;

        BooleanBuilder where = new BooleanBuilder();
        where.and(qContains(p, q));
        where.and(regionIn(p, regions));
        where.and(typeIn(p, types));

        //  content
        List<PlacesFindResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        PlacesFindResponseDto.class,
                        p.id,                 // placeId
                        p.placeName,
                        p.placeImageUrl,
                        p.oneLineDesc,
                        p.placeLotAddress,
                        p.placeRoadAddress,
                        p.placeType,
                        p.likeCount,
                        p.viewCount,
                        p.createdAt
                ))
                .from(p)
                .where(where)
//                .orderBy(p.createdAt.desc())
                .orderBy(p.createdAt.desc(), p.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // total count
        Long total = jpaQueryFactory
                .select(p.count())
                .from(p)
                .where(where)
                .fetchOne();

        long totalElements = (total == null) ? 0L : total;

        return new PageImpl<>(content, pageable, totalElements);
    }

    private BooleanExpression qContains(QPlaces p, String q) {
        if (q == null || q.trim().isEmpty()) return null;
        String qq = q.trim();

        return p.placeName.coalesce("").containsIgnoreCase(qq)
                .or(p.oneLineDesc.coalesce("").containsIgnoreCase(qq))
                .or(p.placeDescription.coalesce("").containsIgnoreCase(qq));
    }

    private BooleanExpression regionIn(QPlaces p, List<String> regions) {
        if (CollectionUtils.isEmpty(regions)) return null; // 조건 제거
        return p.placeRegion.in(regions);
    }

    private BooleanExpression typeIn(QPlaces p, List<String> types) {
        if (CollectionUtils.isEmpty(types)) return null; // 조건 제거
        return p.placeType.in(types);
    }
}
