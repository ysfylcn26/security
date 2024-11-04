package com.business.auth.security.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.business.auth.security.enums.Permission.*;


@Getter
@RequiredArgsConstructor
public enum Role {

  ADMIN(
          Set.of(
                  ADMIN_READ,
                  ADMIN_UPDATE,
                  ADMIN_DELETE,
                  ADMIN_CREATE
          )
  ),
  MANAGER(
          Set.of(
                  MANAGER_READ,
                  MANAGER_UPDATE,
                  MANAGER_DELETE,
                  MANAGER_CREATE
          )
  ),
  USER(
          Set.of(
                  USER_READ,
                  USER_UPDATE,
                  USER_DELETE,
                  USER_CREATE
          )
  );

  private final Set<Permission> permissions;

  @JsonValue
  public String toJson() {
    return this.name();
  }

  @JsonCreator
  public static Role fromJson(String roleName) {
    try {
      return Role.valueOf(roleName.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid role name: " + roleName);
    }
  }

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }

}
