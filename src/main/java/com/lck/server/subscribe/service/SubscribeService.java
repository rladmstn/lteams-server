package com.lck.server.subscribe.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.subscribe.exception.SubscribeValidationException;
import com.lck.server.subscribe.repository.SubscribeRepository;
import com.lck.server.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscribeService {
	private final SubscribeRepository subscribeRepository;
	public void subscribeTeam(User user, Team team) {
		if(subscribeRepository.existsByUserAndTeam(user,team))
			throw new SubscribeValidationException("이미 구독한 팀입니다. : "+team.toString());

		subscribeRepository.save(Subscribe.builder().user(user).team(team)
			.subscriptionOrder(user.getSubscribedTeamCount() + 1)
			.subscribedDate(LocalDateTime.now())
			.permission(false)
			.build());
		user.updateSubscribedTeamCount(1);
		log.info("success to subscribe team");
	}
}
