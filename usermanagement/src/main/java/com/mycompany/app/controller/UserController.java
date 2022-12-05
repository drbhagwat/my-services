package com.mycompany.app.controller;

import com.mycompany.app.model.User;
import com.mycompany.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

  @GetMapping("/get/{userName}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public String get(Model model, @PathVariable("userName") String userName) {
    model.addAttribute("user", userService.get(userName));
    return "user";
  }

  @GetMapping("/add")
  @PreAuthorize("hasAuthority('user:write')")
  public String add(Model model) {
    model.addAttribute("user", new User());
    return "/register";
  }

  @GetMapping("/update")
  @PreAuthorize("hasAuthority('user:write')")
  public ModelAndView update(@RequestParam String userName) {
    User user = userService.find(userName);
    ModelAndView modelAndView = new ModelAndView("update");
    modelAndView.addObject("user", user);
    return modelAndView;
  }

  @PostMapping("/update")
  @PreAuthorize("hasAuthority('user:write')")
  public String update(@Valid User user, BindingResult bindingResult,
                       Model model) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("user", user);
      return "update";
    }
    userService.update(userService.find(user.getUsername()), user);
    return "redirect:/api/v1/users";
  }

  @GetMapping("/delete/{userName}")
  @PreAuthorize("hasAuthority('user:write')")
  public String delete(@PathVariable("userName") String userName) {
    userService.delete(userName);
    return "redirect:/api/v1/users";
  }
}
