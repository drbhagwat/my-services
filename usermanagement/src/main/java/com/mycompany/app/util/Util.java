package com.mycompany.app.util;

import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class Util {
  private final UserService userService;

  @Autowired
  public Util(UserService userService) {
    this.userService = userService;
  }

  public String getLoggedInUserName() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  public User getLoggedInUser() {
    return userService.get(getLoggedInUserName());
  }

  public String getRoleOfLoggedInUser() {
    return getLoggedInUser().getRoles().stream().findFirst().get().getName();
  }

  public int getNumberOfUsers() {
    // get # users in the system
    return userService.getAll().size();
  }
}
