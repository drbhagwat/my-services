package com.mycompany.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;


@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AppGrantedAuthority implements GrantedAuthority {
  /* I had to carve out a surrogate PrimaryKey (PK), as the role by
  itself is not unique. In the current design, the role is a collection of
  permissions. There could be two (or more) users (admin and a normal user)
  with the same permission (user:read for eg.,) although the
  primary roles of these two users are different (i.e., ROLE_ADMIN and
  ROLE_USER)
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String role;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  public AppGrantedAuthority(String role) {
    this.role = role;
  }

  @Override
  public String getAuthority() {
    return this.role;
  }
}
