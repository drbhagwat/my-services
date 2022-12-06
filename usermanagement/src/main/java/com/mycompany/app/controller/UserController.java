package com.mycompany.app.controller;

import com.mycompany.app.model.Role;
import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Stream;

@Controller
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public String getAll(Model model) {
    model.addAttribute("users", userService.getAll());
    return "users";
  }

  @GetMapping("/get")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ModelAndView get(@RequestParam String userName) {
    ModelAndView modelAndView = new ModelAndView("user");
    modelAndView.addObject("user", userService.get(userName));
    return modelAndView;
  }

  @GetMapping("/add")
  @PreAuthorize("hasRole('ADMIN')")
  public String add(Model model) {
    model.addAttribute("user", new User());
    return "/register";
  }

  @GetMapping("/update")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public String update(@RequestParam String userName, Model model) {
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!userName.equals(loggedInUser.getUsername())) {
      return "redirect:/api/v1/users";
    } else {
      int numberOfUsers = userService.getAll().size();
      model.addAttribute("numberOfUsers", numberOfUsers);
      User user = userService.find(userName);
      model.addAttribute("user", user);
    /*
      dropdown-list of all roles (defined for all user types) in the system
      is populated (but only for second and subsequent users). As of now,
      ADMIN, and USER roles exist. This could be extended to other types of
      users. Note that the very first user should be ADMIN
     */
      if (!user.getRole().equals("USER")) {
        model.addAttribute("roles",
            Stream.of(Role.values()).map(Role::name).toList());
      }
    }
    return "update";
  }

  @PostMapping("/update")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public String update(@Valid User user, BindingResult bindingResult,
                       Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", user);
      return "update";
    }
    userService.update(user);
    return "redirect:/api/v1/users?success";
  }

  @GetMapping("/delete")
  @PreAuthorize("hasRole('ADMIN')")
  public String delete(@RequestParam String userName) {
    userService.delete(userName);
    return "redirect:/api/v1/users";
  }
}
