package com.lck.server.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lck.server.user.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);

	Optional<User> findByEmail(String email);
}
