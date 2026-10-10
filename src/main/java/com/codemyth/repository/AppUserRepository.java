package com.codemyth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codemyth.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	@EntityGraph(attributePaths = "roles")
	Optional<AppUser> findByUsername(String username);

	boolean existsByUsername(String username);
}
