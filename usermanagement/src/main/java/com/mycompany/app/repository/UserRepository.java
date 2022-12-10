package com.mycompany.app.repository;

import com.mycompany.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  @Query("from User u left join u.roles r where r.name = :#{#role}")
  List<User> findUsersWithSpecificRole(@Param("role") String role);
}
