package com.lck.server.subscribe.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lck.server.enumerate.Role;
import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.subscribe.exception.SubscribeValidationException;
import com.lck.server.subscribe.repository.SubscribeRepository;
import com.lck.server.user.domain.User;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest {
	@InjectMocks
	private SubscribeService subscribeService;
	@Mock
	private SubscribeRepository subscribeRepository;

	private User user;
	@Captor
	private ArgumentCaptor<Subscribe> subscribeCaptor;
	@BeforeEach
	void setUp() {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("profileImage").subscribedTeamCount(0)
			.nicknameChangeableDate(LocalDate.now()).role(Role.USER).build();
	}
	@Test
	@DisplayName("팀 구독 성공")
	void subscribeTeam() {
		// given
		// when
		subscribeService.subscribeTeam(user, Team.T1);
		// then
		verify(subscribeRepository, times(1)).save(subscribeCaptor.capture());
		Subscribe res = subscribeCaptor.getValue();
		assertThat(res.getTeam()).isEqualTo(Team.T1);
		assertThat(res.getUser()).isEqualTo(user);
		assertThat(res.getUser().getSubscribedTeamCount()).isEqualTo(1);
		assertThat(res.getSubscriptionOrder()).isEqualTo(1);
	}

	@Test
	@DisplayName("팀 구독 실패 : 이미 구독한 팀")
	void subscribeTeamFailedSubscribeValidationException(){
		// given
		when(subscribeRepository.existsByUserAndTeam(user,Team.T1)).thenReturn(true);
		// when, then
		assertThatThrownBy(() -> subscribeService.subscribeTeam(user,Team.T1))
			.isInstanceOf(SubscribeValidationException.class)
			.hasFieldOrPropertyWithValue("error","이미 구독한 팀입니다. : "+Team.T1);
	}
}