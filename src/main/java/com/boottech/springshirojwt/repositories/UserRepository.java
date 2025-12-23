package com.boottech.springshirojwt.repositories;

import com.boottech.springshirojwt.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the {@link User} JPA entity. Any custom methods, not
 * already defined in {@link JpaRepository}, are to be defined here
 */
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByUsername(String username);
	Optional<User> findByUsernameAndEnabledTrue(String username);
}
