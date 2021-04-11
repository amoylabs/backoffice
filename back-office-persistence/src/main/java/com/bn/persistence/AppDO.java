package com.bn.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class AppDO {
    private String id;
    private String name;
    private String signingKey;
    private ZonedDateTime createdTime;
    private String createdBy;
    private ZonedDateTime updatedTime;
    private String updatedBy;
}
