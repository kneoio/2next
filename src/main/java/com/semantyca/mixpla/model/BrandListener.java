package com.semantyca.mixpla.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class BrandListener {
    private UUID id;
    private UUID brandId;
    private int rank;
    private Listener listener;
}