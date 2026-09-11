package com.khan.EComm.repo;

import com.khan.EComm.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The Database Adapter Implementation.
 * 
 * This class implements our custom domain interface (`UserRepository`) and acts as a bridge.
 * It takes the generic calls from `UserService` and delegates them to the actual 
 * Spring Data JPA repository (`JpaUserRepository`) to execute the database operations.
 */
@Repository
public class SQLUserRepository implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public SQLUserRepository(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id);
    }
}
