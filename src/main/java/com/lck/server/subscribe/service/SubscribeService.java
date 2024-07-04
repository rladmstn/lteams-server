package com.lck.server.subscribe.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.subscribe.dto.GetSubscribeResponse;
import com.lck.server.subscribe.exception.SubscribeValidationException;
import com.lck.server.subscribe.repository.SubscribeRepository;
import com.lck.server.user.domain.User;
import com.lck.server.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscribeService {
	private final SubscribeRepository subscribeRepository;
	private final UserRepository userRepository;
	public void subscribeTeam(User targetUser, Team team) {
		User user = userRepository.findById(targetUser.getId()).orElseThrow();

		if(subscribeRepository.existsByUserAndTeam(user,team))
			throw new SubscribeValidationException("이미 구독한 팀입니다. : "+team.toString());

		subscribeRepository.save(Subscribe.builder().user(user).team(team)
			.subscriptionOrder(user.getSubscribedTeamCount()+1)
			.subscribedDate(LocalDateTime.now())
			.permission(false)
			.build());
		user.updateSubscribedTeamCount(user.getSubscribedTeamCount()+1);
		log.info("success to subscribe team");
	}

	public List<GetSubscribeResponse> getSubscribedTeamList(User user){
		List<Subscribe> subscribes = subscribeRepository.findAllByUser(user);
		List<GetSubscribeResponse> result = subscribes.stream().map(GetSubscribeResponse::toDTO).toList();
		log.info("success to get subscribed team list");
		return result;
	}
}
