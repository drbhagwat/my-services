package com.mycompany.app.controller;

import com.mycompany.app.enums.UserType;
import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {
  private final UserService userService;

  public RegistrationController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public String register(Model model) {
    int numberOfUsers = getNumberOfUsers();
    model.addAttribute("numberOfUsers", numberOfUsers);
    model.addAttribute("user", new User());
    /*
      dropdown-list of all user types in the system is populated (but for
      the second and subsequent users only). As of now, ADMIN, and USER
      types exist. This could be extended to other types of users. Note that
      the very first user should be ADMIN and that is enforced by the system.
     */
    if (numberOfUsers != 0) {
      model.addAttribute("roles", UserType.values());
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
      user.setRole(UserType.ADMIN.getUserRole());
    }
    boolean isUserAdded = userService.add(user);

    return isUserAdded? "redirect:/register?success": "redirect:/register" +
        "?emailExists";
  }

  public int getNumberOfUsers() {
    // get # users in the system
    return userService.getAll().size();
  }
}
