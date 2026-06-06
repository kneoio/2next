package com.semantyca.mixpla.model.brand;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Owner {
    private Long userId;
    private String name;
    private String email;
    private boolean exposeWhileSharing;
    private boolean actionDebugEnabled;
    private List<Owner> coOwners;
}
