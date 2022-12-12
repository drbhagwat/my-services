package com.mycompany.app.service;

import com.mycompany.app.model.Role;
import com.mycompany.app.model.User;
import com.mycompany.app.repository.RoleRepository;
import com.mycompany.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserService(PasswordEncoder passwordEncoder,
                     UserRepository userRepository,
                     RoleRepository roleRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String userName)
      throws UsernameNotFoundException {
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
    String userName = newUser.getUsername();
    User existingUser = userRepository.findById(userName).get();
    /* password input by end user is plain text, encrypt it */
    existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

    if ((getAll().size() > 1) && (newUser.getRole() != null)) {
      existingUser.setRole(newUser.getRole()); // role might have changed
    }
    userRepository.save(existingUser);
  }

  public void delete(String userName) {
    User user = find(userName);
    // get the role of the user, currently a user can have only one role
    Role role = user.getRoles().stream().findFirst().get();
    // get list of users from the repo who have the same role
    List<User> usersWithSpecificRole =
        userRepository.findUsersWithSpecificRole(role.getName());

    // if this is the only user, delete the role to conserve some space
    if (usersWithSpecificRole.size() == 1) {
      roleRepository.delete(role);
    }
    user.removeRole(role);
    userRepository.delete(user);
  }

  public void init(User user) {
    /* password input by end user is plain text, encrypt it */
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    /* set other fields to true by default */
    user.setAccountNonExpired(true);
    user.setAccountNonLocked(true);
    user.setEnabled(true);
    user.setCredentialsNonExpired(true);
    user.addRole(new Role("ROLE_" + user.getRole()));
  }
}
