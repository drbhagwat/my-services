package com.mycompany.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
  @Id
  @NotNull(message = "role cannot be null")
  @NotEmpty(message = "role cannot be empty")
  private String name;

  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "roles")
  private Set<User> users = new HashSet<>();

  public Role(String name) {
    this.name = name;
  }
}
