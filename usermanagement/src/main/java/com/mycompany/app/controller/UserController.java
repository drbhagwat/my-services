package com.mycompany.app.controller;

import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import com.mycompany.app.util.Util;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public String getAll(Model model) {
    model.addAttribute("loggedInUser",
        (new Util(userService)).getLoggedInUser());
    model.addAttribute("users", userService.getAll());
    return "users";
  }

  @GetMapping("get")
  public ModelAndView get(@RequestParam String userName) {
    ModelAndView modelAndView = new ModelAndView("user");
    modelAndView.addObject("user", userService.get(userName));
    return modelAndView;
  }

  @GetMapping("add")
  public String add(Model model) {
    model.addAttribute("loggedInUser",
        (new Util(userService)).getLoggedInUser());
    model.addAttribute("user", new User());
    return "/register";
  }

  @GetMapping("update")
  public String update(@RequestParam String userName, Model model) {

    if (!userName.equals(new Util(userService).getLoggedInUserName())) {
      return "redirect:/api/v1/users"; // for now it is ignored...
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
      if (! (new Util(userService)).getRoleOfLoggedInUser().equals("USER")) {
        model.addAttribute("roles", List.of("ADMIN", "USER"));
      }
    }
    return "update";
  }

  @PostMapping("update")
  public String update(@Valid User newUser, BindingResult bindingResult,
                       Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", newUser);
      return "update";
    }
    userService.update(newUser);
    return "redirect:/api/v1/users?success";
  }

  @GetMapping("delete")
  public String delete(@RequestParam String userName) {
    userService.delete(userName);
    return "redirect:/api/v1/users";
  }
}
