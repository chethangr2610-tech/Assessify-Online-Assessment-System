package com.example.exam.repository;

import com.example.exam.model.Group;
import com.example.exam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Find groups managed by a specific admin
    List<Group> findByAdmin(User admin);
    
    // Find group by name and admin
    Group findByNameAndAdmin(String name, User admin);
}
