package com.mycompany.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Permission {
  USER_READ("user:read"),
  USER_WRITE("user:write");

  private final String permission;
}
