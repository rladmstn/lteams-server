package com.lck.server.user.repository;

import java.util.Optional;

import com.lck.server.user.domain.User;

public interface UserCustomRepository {
	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);
	Optional<User> findByEmail(String email);
}
