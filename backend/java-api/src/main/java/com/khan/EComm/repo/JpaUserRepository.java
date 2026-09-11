package com.khan.EComm.repo;

import com.khan.EComm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The Spring Data Repository Interface.
 * 
 * Note on naming: This interface does NOT extend itself! 
 * It extends `org.springframework.data.jpa.repository.JpaRepository`, which is a built-in Spring class.
 * Spring uses this interface to automatically generate SQL queries at runtime (like findByEmail).
 * This interface is meant to be used ONLY by our SQLUserRepository adapter, hiding Spring Data from the rest of the app.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
