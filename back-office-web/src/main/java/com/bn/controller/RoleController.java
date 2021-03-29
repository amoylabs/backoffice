package com.bn.controller;

import com.bn.authorization.UserAuthorizationRequired;
import com.bn.authorization.UserRealm;
import com.bn.authorization.UserRealmContextHolder;
import com.bn.controller.request.role.CreateRealmRequest;
import com.bn.controller.request.role.CreateRoleRealmRequest;
import com.bn.controller.request.role.CreateRoleRequest;
import com.bn.controller.response.role.GetRoleRealmsResponse;
import com.bn.controller.response.role.ListRealmsResponse;
import com.bn.controller.response.role.ListRolesResponse;
import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.domain.RoleRealmSetting;
import com.bn.service.RoleService;
import com.bn.web.validation.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Validated
public class RoleController {
    private RoleService roleService;

    @GetMapping
    public ListRolesResponse listRoles() {
        List<ListRolesResponse.RoleVO> roleViewList = roleService.listRoles().stream().map(role -> ListRolesResponse.RoleVO.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .build()).collect(Collectors.toList());
        return ListRolesResponse.builder().roles(roleViewList).build();
    }

    @PostMapping
    @UserAuthorizationRequired("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    public String createRole(@Valid @RequestBody CreateRoleRequest request) {
        Role role = Role.builder().name(request.getName()).description(request.getDescription()).build();
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        return roleService.createRole(role, context.getUserName());
    }

    @GetMapping("realms")
    public ListRealmsResponse listRealms() {
        List<ListRealmsResponse.RealmVO> realmViewList = roleService.listRealms().stream().map(realm -> ListRealmsResponse.RealmVO.builder()
            .id(realm.getId())
            .name(realm.getName())
            .description(realm.getDescription())
            .build()).collect(Collectors.toList());
        return ListRealmsResponse.builder().roles(realmViewList).build();
    }

    @PostMapping("realms")
    @UserAuthorizationRequired("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    public String createRealm(@Valid @RequestBody CreateRealmRequest request) {
        Realm realm = Realm.builder().name(request.getName()).description(request.getDescription()).build();
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        return roleService.createRealm(realm, context.getUserName());
    }

    @GetMapping("{id}/realms")
    public GetRoleRealmsResponse getRealms4Role(@PathVariable @UUID String id) {
        RoleRealmSetting setting = roleService.getRealms4Role(id);
        Role role = setting.getRole();
        List<GetRoleRealmsResponse.RealmVO> realmViews = setting.getRealms().stream()
            .map(realm -> GetRoleRealmsResponse.RealmVO.builder().id(realm.getId()).name(realm.getName()).build()).collect(Collectors.toList());
        return GetRoleRealmsResponse.builder()
            .roleId(role.getId())
            .roleName(role.getName())
            .realms(realmViews)
            .build();
    }

    @PostMapping("realm-settings")
    @UserAuthorizationRequired("ADMIN")
    public void saveOrUpdateRoleRealmsSettings(@Valid @RequestBody CreateRoleRealmRequest request) {
        List<RoleRealmSetting> settings = request.getRoleRealms().stream().map(roleRealm -> {
            List<Realm> realms = roleRealm.getRealmIds().stream().map(realmId -> Realm.builder().id(realmId).build()).collect(Collectors.toList());
            return RoleRealmSetting.builder().role(Role.builder().id(roleRealm.getRoleId()).build()).realms(realms).build();
        }).collect(Collectors.toList());
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        roleService.saveOrUpdateRoleRealmsSettings(settings, context.getUserName());
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
