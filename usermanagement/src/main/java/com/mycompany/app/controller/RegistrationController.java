package com.mycompany.app.controller;

import com.mycompany.app.model.Role;
import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    User user = new User();

    /*
      dropdown-list of roles is not populated for the very first-user.
      First-user should always be an ADMIN.
     */
    if (numberOfUsers == 0) {
      user.setRole("ADMIN");
    } else {
    /*
      dropdown-list is populated with all roles of users in the system
      For now only ADMIN, and USER but could be extended to other types of
      users.
     */
      model.addAttribute("roles",
          Stream.of(Role.values()).map(Role::name).toList());
    }
    model.addAttribute("user", user);
    return "register";
  }

  @PostMapping
  public String register(@ModelAttribute("user") User user) {
    if (getNumberOfUsers() == 0) {
      user.setRole("ADMIN");
    }
    String error = userService.add(user);

    if (error == null) {
      return "redirect:/register?success";
    } else {
      return "redirect:/register?emailExists";
    }
  }

  private int getNumberOfUsers() {
    // get the number of users in the current system
    return userService.getAll().size();
  }
}
