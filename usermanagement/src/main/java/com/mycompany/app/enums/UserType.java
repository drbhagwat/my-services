package com.mycompany.app.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {
  ADMIN("ADMIN"), USER ("USER");

  private String userRole;
}
