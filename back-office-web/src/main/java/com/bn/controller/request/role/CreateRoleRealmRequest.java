package com.bn.controller.request.role;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
public class CreateRoleRealmRequest {
    @NotNull(message = "role and realms are both required")
    @Size(min = 1, message = "role and realms are both required")
    @Valid
    private List<RoleRealm> roleRealms;

    @Setter
    @Getter
    public static class RoleRealm {
        @NotBlank(message = "role is required")
        private String roleId;

        @NotNull(message = "realms are required")
        @Size(min = 1, message = "realms are required")
        private List<String> realmIds;
    }
}
