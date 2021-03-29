package com.bn.controller.request.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRealmRequest {
    @JsonProperty("role_realms")
    @NotNull(message = "role and realms are both required")
    @Size(min = 1, message = "role and realms are both required")
    @Valid
    private List<RoleRealm> roleRealms;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleRealm {
        @JsonProperty("role_id")
        @NotBlank(message = "role is required")
        private Long roleId;

        @JsonProperty("realm_ids")
        @NotNull(message = "realms are required")
        @Size(min = 1, message = "realms are required")
        private List<Long> realmIds;
    }
}
