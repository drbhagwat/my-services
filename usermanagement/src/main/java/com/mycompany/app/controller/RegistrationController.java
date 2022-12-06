package com.mycompany.app.controller;

import com.mycompany.app.model.Role;
import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Stream;

@Controller
@RequestMapping("/register")
public class RegistrationController {
  private final UserService userService;

  @Autowired
  public RegistrationController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public String register(Model model) {
    int numberOfUsers = getNumberOfUsers();
    model.addAttribute("numberOfUsers", numberOfUsers);
    model.addAttribute("user", new User());
    /*
      dropdown-list of all roles (defined for all user types) in the system
      is populated (but only for second and subsequent users). As of now,
      ADMIN, and USER roles exist. This could be extended to other types of
      users. Note that the very first user should be ADMIN
     */
    if (numberOfUsers != 0) {
      model.addAttribute("roles", Stream.of(Role.values()).map(Role::name).toList());
    }
    return "register";
  }

  @PostMapping
  public String register(@Valid User user, BindingResult bindingResult,
                         Model model) {
    int numberOfUsers = getNumberOfUsers();

    if (bindingResult.hasErrors()) {
      model.addAttribute("user", user);
      model.addAttribute("numberOfUsers", numberOfUsers);
      return "register";
    } /*
      dropdown-list of roles is not populated for the very first-user.
      First-user should always be an ADMIN.
      */
    if (numberOfUsers == 0) {
      user.setRole("ADMIN");
    }
    boolean isUserAdded = userService.add(user);

    if (isUserAdded) {
      return "redirect:/register?success";
    } else {
      return "redirect:/register?emailExists";
    }
  }

  public int getNumberOfUsers() {
    // get the number of users in the current system
    return userService.getAll().size();
  }
}
