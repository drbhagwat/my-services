package com.mycompany.app.controller;

import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public String getAll(Model model) {
    model.addAttribute("users", userService.getAll());
    return "users";
  }

  @GetMapping(path = "{userName}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public String get(Model model, @PathVariable("userName") String userName) {
    model.addAttribute("user", userService.get(userName));
    return "user";
  }

  @GetMapping("/add")
  @PreAuthorize("hasAuthority('user:write')")
  public String add(Model model) {
    User user = new User();
    model.addAttribute("user", user);
    return "add-user";
  }

  @PostMapping("/add")
  @PreAuthorize("hasAuthority('user:write')")
  public String add(@ModelAttribute User user) {
    userService.add(user);
    return "redirect:/api/v1/users";
  }

  @PutMapping(path = "{userName}")
  @PreAuthorize("hasAuthority('user:write')")
  public void update(@PathVariable("userName") String userName,
                     @RequestParam(required = false) String newUserName) {
    userService.update(userName, newUserName);
  }

  @DeleteMapping(path = "{userName}")
  @PreAuthorize("hasAuthority('user:write')")
  public void delete(@PathVariable("userName") String userName) {
    userService.delete(userName);
  }
}
