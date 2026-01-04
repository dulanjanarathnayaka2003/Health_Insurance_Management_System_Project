package com.sliit.healthins.repository;

import com.sliit.healthins.model.Role;
import com.sliit.healthins.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface UserRepository extends JpaRepository<User, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u LEFT JOIN FETCH u.policies WHERE u.id = :id")
    Optional<User> findById(Long id);
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u LEFT JOIN FETCH u.policies WHERE u.username = :username")
    Optional<User> findByUsername(String username);
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    List<User> findByNameContainingIgnoreCase(String name);
    long countByRole(Role role);
    boolean existsByEmail(String email);
    List<User> findByNameContainingIgnoreCaseOrContactContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String contact, String email);

    boolean existsByUsername(String username);
    long countByIsActiveTrue();
    List<User> findByRole(Role role);
}