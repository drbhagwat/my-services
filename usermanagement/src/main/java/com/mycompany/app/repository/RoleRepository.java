package com.mycompany.app.repository;

import com.mycompany.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String>  {
}
