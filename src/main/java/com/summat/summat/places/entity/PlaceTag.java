package com.summat.summat.places.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PlaceTag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Places place;

    @ManyToOne
    private Tag tag;
}
