package com.lck.server.subscribe.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.subscribe.dto.GetSubscribeResponse;
import com.lck.server.subscribe.exception.SubscribeValidationException;
import com.lck.server.subscribe.repository.SubscribeRepository;
import com.lck.server.user.domain.User;
import com.lck.server.user.exception.UserValidationException;
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

	public void unsubscribeTeam(User targetUser, Long subscribeId) {
		User user = userRepository.findById(targetUser.getId()).orElseThrow();
		Subscribe subscribe = subscribeRepository.findById(subscribeId)
			.orElseThrow(() -> new SubscribeValidationException("이미 구독하지 않은 팀 입니다."));

		if(!user.getId().equals(subscribe.getUser().getId()))
			throw new UserValidationException("구독 취소에 대한 권한이 없습니다.");

		List<Subscribe> subscribes = subscribeRepository.findListGreaterThanOrder(user,subscribe.getSubscriptionOrder());
		subscribes.forEach(Subscribe::updateSubscriptionOrder);

		subscribeRepository.delete(subscribe);
		user.updateSubscribedTeamCount(user.getSubscribedTeamCount()-1);
		log.info("success to unsubscribe team");
	}

	@Scheduled(cron = "0 0/1 * * * *")
	public void checkSubscribePermission(){
		List<Subscribe> subscribes = subscribeRepository.findAfterPermissionDate(LocalDateTime.now());
		for(Subscribe subscribe : subscribes)
			subscribe.grantPermission();
	}
}
