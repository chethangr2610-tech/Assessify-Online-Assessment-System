package com.example.exam.repository;

import com.example.exam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(String role);
    List<User> findByCreatedByAndRole(User createdBy, String role);
    List<User> findByGroupIn(List<com.example.exam.model.Group> groups);
    long countByRole(String role);
}
