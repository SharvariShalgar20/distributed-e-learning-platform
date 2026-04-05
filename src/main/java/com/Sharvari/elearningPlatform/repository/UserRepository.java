package com.Sharvari.elearningPlatform.repository;

import com.Sharvari.elearningPlatform.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);
    Optional<User> findById(String userId);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void delete(String userId);
    boolean existsByEmail(String email);
}
