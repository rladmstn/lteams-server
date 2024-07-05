package com.lck.server.subscribe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.user.domain.User;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long>, SubscribeCustomRepository {
	boolean existsByUserAndTeam(User user, Team team);
	List<Subscribe> findAllByUser(User user);
}
