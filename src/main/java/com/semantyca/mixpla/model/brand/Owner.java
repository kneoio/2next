package com.semantyca.mixpla.model.brand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Owner {
    private Long userID;
    private String name;
    private String email;
    private boolean exposeWhileSharing;
    private boolean actionDebugEnabled;
}
