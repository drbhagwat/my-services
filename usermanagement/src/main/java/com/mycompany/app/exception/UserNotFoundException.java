package com.mycompany.app.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String userName) {
    super("Could not find the user with the following email " + userName);
  }
}
