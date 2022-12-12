package com.mycompany.app;

import com.mycompany.app.audit.AuditorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.mycompany.app")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Main {
  @Bean
  public AuditorAware<String> auditorAware() {
    return new AuditorAwareImpl();
  }

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
