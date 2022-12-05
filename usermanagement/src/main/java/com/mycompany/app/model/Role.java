package com.mycompany.app.model;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

import static com.mycompany.app.model.Permission.USER_READ;
import static com.mycompany.app.model.Permission.USER_WRITE;

@AllArgsConstructor
@Getter
public enum Role {
  ADMIN(Sets.newHashSet(USER_READ, USER_WRITE)),
  USER(Sets.newHashSet(USER_READ));
  private final Set<Permission> permissions;

  public Set<AppGrantedAuthority> getGrantedAuthorities() {
    Set<AppGrantedAuthority> appGrantedAuthorities =
        getPermissions().stream().map(permission -> new AppGrantedAuthority(permission.getPermission())).collect(Collectors.toSet());
    appGrantedAuthorities.add(new AppGrantedAuthority("ROLE_" + this.name()));
    return appGrantedAuthorities;
  }
}
