package com.mycompany.app.service;

import com.mycompany.app.exception.UserNotFoundException;
import com.mycompany.app.model.AppGrantedAuthority;
import com.mycompany.app.model.User;
import com.mycompany.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.mycompany.app.model.Role.ADMIN;
import static com.mycompany.app.model.Role.USER;

@Service
public class UserService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  @Autowired
  public UserService(PasswordEncoder passwordEncoder,
                     UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    return userRepository.findById(userName)
        .orElseThrow(() -> new UsernameNotFoundException(String.format("User " +
            "name %s not found", userName)));
  }

  public User find(String userName) {
    Optional<User> optionalUser =
        userRepository.findById(userName);

    if (optionalUser.isEmpty()) {
      throw new UserNotFoundException(userName);
    }
    return optionalUser.get();
  }

  public User get(String userName) {
    return find(userName);
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public String add(User user) {
    String userName = user.getUsername();
    Optional<User> optionalUser =
        userRepository.findById(userName);

    if (!optionalUser.isEmpty()) {
      return "User with the specified email already exists ";
    }

    /* password input by end user is plain text */
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    /* set other member-fields to true by default */
    user.setAccountNonExpired(true);
    user.setAccountNonLocked(true);
    user.setEnabled(true);
    user.setCredentialsNonExpired(true);

    Set<AppGrantedAuthority> appGrantedAuthorities = null;

    if (user.getRole().equals("USER")) {
      appGrantedAuthorities = USER.getGrantedAuthorities();
    } else {
      appGrantedAuthorities = ADMIN.getGrantedAuthorities();
    }

    for (AppGrantedAuthority appGrantedAuthority : appGrantedAuthorities) {
      user.addAppGrantedAuthority(appGrantedAuthority);
    }
    // Save the user - this also saves the set of
    // permissions in the related child table.
    userRepository.save(user);
    return null; // everything worked fine, return null */
  }

  @Transactional
  public void update(String userName, String newUserName) {
    User user = find(userName);

    if ((userName != null) && (userName.length() > 0) &&
        (!user.getUsername().equals(newUserName))) {
      user.setUsername(newUserName);
    }
  }

  public void delete(String userName) {
    User user = find(userName);
    userRepository.delete(user);
  }
}
