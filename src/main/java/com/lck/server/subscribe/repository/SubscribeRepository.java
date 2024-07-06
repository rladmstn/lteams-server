package com.lck.server.subscribe.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.user.domain.User;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long>, SubscribeCustomRepository {
	boolean existsByUserAndTeam(User user, Team team);
	List<Subscribe> findAllByUser(User user);
	@Query(value = "SELECT * FROM Subscribe s "
		+ "WHERE DATE_ADD(s.subscribed_date, INTERVAL (14 * s.subscription_order) DAY) < :curDate", nativeQuery = true)
	List<Subscribe> findAfterPermissionDate(LocalDateTime curDate);
}
