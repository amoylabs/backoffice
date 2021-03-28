package com.bn.domain;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Realm {
    private String id;
    private String name;
    private String description;

    public void initialize() {
        setId(UUID.randomUUID().toString());
    }
}
