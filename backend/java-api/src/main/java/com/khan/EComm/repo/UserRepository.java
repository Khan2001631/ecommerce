package com.khan.EComm.repo;

import com.khan.EComm.model.User;
import java.util.List;
import java.util.Optional;

/**
 * The Domain Interface (Port) for User persistence.
 * This interface isolates our core business logic (UserService) from any database-specific frameworks.
 * By relying on this plain Java interface, our service layer remains completely unaware of whether 
 * the underlying database is SQL, NoSQL, or something else.
 */
public interface UserRepository {
    User save(User user);
    User findByEmail(String email);
    List<User> findAll();
    Optional<User> findById(Long id);
}
