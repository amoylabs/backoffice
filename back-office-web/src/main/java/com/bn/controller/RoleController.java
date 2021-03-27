package com.bn.controller;

import com.bn.authorization.UserAuthorizationRequired;
import com.bn.authorization.UserRealm;
import com.bn.authorization.UserRealmContextHolder;
import com.bn.controller.request.CreateRealmRequest;
import com.bn.controller.request.CreateRoleRequest;
import com.bn.controller.response.RealmVO;
import com.bn.controller.response.RoleVO;
import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping("v1/roles")
@RestController
@Slf4j
public class RoleController {
    private RoleService roleService;

    @GetMapping
    public List<RoleVO> listRoles() {
        return roleService.listRoles().stream().map(role -> RoleVO.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .build()).collect(Collectors.toList());
    }

    @PostMapping
    @UserAuthorizationRequired("admin")
    @ResponseStatus(HttpStatus.CREATED)
    public String createRole(@Valid @RequestBody CreateRoleRequest request) {
        Role role = Role.builder().name(request.getName()).description(request.getDescription()).build();
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        return roleService.createRole(role, context.getUserName());
    }

    @GetMapping("realms")
    public List<RealmVO> listRealms() {
        return roleService.listRealms().stream().map(realm -> RealmVO.builder()
            .id(realm.getId())
            .name(realm.getName())
            .description(realm.getDescription())
            .build()).collect(Collectors.toList());
    }

    @PostMapping("realms")
    @UserAuthorizationRequired("admin")
    @ResponseStatus(HttpStatus.CREATED)
    public String createRealm(@Valid @RequestBody CreateRealmRequest request) {
        Realm realm = Realm.builder().name(request.getName()).description(request.getDescription()).build();
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        return roleService.createRealm(realm, context.getUserName());
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
