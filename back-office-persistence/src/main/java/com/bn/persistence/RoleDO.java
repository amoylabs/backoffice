package com.bn.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class RoleDO {
    private String id;
    private String name;
    private String description;
    private ZonedDateTime createdTime;
    private String createdBy;
    private ZonedDateTime updatedTime;
    private String updatedBy;
}
