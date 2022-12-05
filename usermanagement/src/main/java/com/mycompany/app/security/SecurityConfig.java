package com.mycompany.app.security;

import com.mycompany.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  @Autowired
  public SecurityConfig(PasswordEncoder passwordEncoder,
                        UserService userService) {
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  @Bean
  public SecurityFilterChain
  securityFilterChain(HttpSecurity http) throws Exception {
    final String[] publicUrls = {"/login", "/register", "/css/**",
        "/js/**", "/images/**"};
    final String listOfUsersUrl = "/api/v1/users";
    final String logoutUrl = "/logout";
    final String logoutSuccessUrl = "/login?logout";
    final String secret = "deepsecretoverridingspringdefault";
    final String[] cookies = {"remember-me", "JSESSIONID"};
    final int rememberMeValidityInSeconds = (int) TimeUnit.DAYS.toSeconds(21);

    http.authenticationProvider(daoAuthenticationProvider());

    return
        http.authorizeHttpRequests()
            .requestMatchers(publicUrls)
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl(listOfUsersUrl, true)
            .and()
            .rememberMe()
            .tokenValiditySeconds(rememberMeValidityInSeconds)
            .key(secret)
            .and()
            .logout()
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl))
            .logoutSuccessUrl(logoutSuccessUrl)
            .deleteCookies(cookies)
            .and().build();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider =
        new DaoAuthenticationProvider();
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    daoAuthenticationProvider.setUserDetailsService(userService);
    return daoAuthenticationProvider;
  }
}
