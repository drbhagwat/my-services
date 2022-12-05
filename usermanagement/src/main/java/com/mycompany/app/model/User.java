package com.mycompany.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

@Entity
/* user is a reserved word in postgreSQL and hence using users */
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
  @Id
  @NotEmpty
  @NotNull
  @Email
  private String username;

  @NotEmpty(message = "password cannot be empty")
  @NotNull(message = "password cannot be null")
  @Size(min = 8, message = "password should have at least " +
      " 8 characters")
  private String password;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval =
      true, fetch = FetchType.EAGER)
  private Collection<AppGrantedAuthority> appGrantedAuthorities =
      new HashSet<>();

  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean enabled;
  private boolean credentialsNonExpired;
  private String role;

  @Override
  public Collection<AppGrantedAuthority> getAuthorities() {
    return appGrantedAuthorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void addAppGrantedAuthority(AppGrantedAuthority appGrantedAuthority) {
    appGrantedAuthorities.add(appGrantedAuthority);
    appGrantedAuthority.setUser(this);
  }
}
