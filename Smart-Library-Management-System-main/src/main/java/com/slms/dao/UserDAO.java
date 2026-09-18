package com.slms.dao;

import com.slms.model.User;
import java.util.List;
import java.util.Optional;

/** Data-access contract for User persistence (DAO design pattern). */
public interface UserDAO {
    List<User> findAll();
    Optional<User> findById(String userId);
    Optional<User> findByUsername(String username);
    String add(User user); // returns generated id
    boolean update(User user);
    boolean delete(String userId);
}
