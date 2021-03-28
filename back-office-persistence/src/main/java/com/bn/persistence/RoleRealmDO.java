package com.bn.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class RoleRealmDO {
    private String id;
    private String roleId;
    private String realmId;
    private ZonedDateTime createdTime;
    private String createdBy;
}
