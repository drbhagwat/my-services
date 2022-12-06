package com.mycompany.app.service;

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

import java.util.Collection;
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
    return optionalUser.isEmpty() ? null : optionalUser.get();
  }

  public User get(String userName) {
    return find(userName);
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public boolean add(User user) {
    String userName = user.getUsername();
    Optional<User> optionalUser = userRepository.findById(userName);

    if (!optionalUser.isEmpty()) {
      return false;
    }
    init(user);
    // When you save the user, the set of permissions in the related
    // child table are also automatically saved.
    userRepository.save(user);
    return true; // everything worked fine, so return true */
  }

  @Transactional
  public void update(User newUser) {
    String newUsername = newUser.getUsername();
    User existingUser = userRepository.findById(newUsername).get();
    /* password input by end user is plain text, encrypt it */
    existingUser.setPassword(passwordEncoder.encode(newUsername));

    if( getAll().size() > 1) {

      if(newUser.getRole() != null) {
        existingUser.setRole(newUser.getRole()); // role might have changed
      }
      updatePermissions(existingUser);
    }
    userRepository.save(existingUser);
  }

  public void delete(String userName) {
    User user = find(userName);

    if (user != null) {
      userRepository.delete(user);
    }
  }

  public void init(User user) {
    /* password input by end user is plain text, encrypt it */
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    /* set other fields to true by default */
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
  }

  public void updatePermissions(User user) {
    Collection<AppGrantedAuthority> appGrantedAuthorities = null;

    if (user.getRole().equals("USER")) {
      appGrantedAuthorities = USER.getGrantedAuthorities();
    } else {
      appGrantedAuthorities = ADMIN.getGrantedAuthorities();
    }
    for (AppGrantedAuthority appGrantedAuthority : appGrantedAuthorities) {
      user.addAppGrantedAuthority(appGrantedAuthority);
    }
  }
}
