package com.summat.summat.places.entity;

import com.summat.summat.enums.PlaceTagType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PlaceTag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

    @Enumerated(EnumType.STRING)
    @Column(name="tag_type", nullable=false)
    private PlaceTagType tagType;
}
