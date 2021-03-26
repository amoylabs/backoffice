package com.bn.persistence;

import com.bn.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
public class UserDO {
    private Long id;
    private String name;
    private String mobilePhone;
    private String email;
    private String password;
    private String passwordSalt;
    private UserStatus status;
    private String roleId;
    private ZonedDateTime createdTime;
    private String createdBy;
    private ZonedDateTime updatedTime;
    private String updatedBy;
}
