package com.lck.server.subscribe.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.lck.server.subscribe.dto.GetSubscribeResponse;
import com.lck.server.subscribe.exception.SubscribeValidationException;
import com.lck.server.subscribe.repository.SubscribeRepository;
import com.lck.server.user.domain.User;
import com.lck.server.user.exception.UserValidationException;
import com.lck.server.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest {
	@InjectMocks
	private SubscribeService subscribeService;
	@Mock
	private SubscribeRepository subscribeRepository;
	@Mock
	private UserRepository userRepository;

	private User user;
	private User diffUser;
	private Subscribe subscribe;
	@Captor
	private ArgumentCaptor<Subscribe> subscribeCaptor;
	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("profileImage").subscribedTeamCount(1)
			.nicknameChangeableDate(LocalDate.now()).role(Role.USER).build();
		diffUser = User.builder().email("diffEmail").password("diffPassword").nickname("diffNickname").profileImage("diffProfileImage").subscribedTeamCount(1)
			.nicknameChangeableDate(LocalDate.now()).role(Role.USER).build();
		subscribe = Subscribe.builder().user(user).team(Team.T1).subscribedDate(LocalDateTime.now())
			.subscriptionOrder(1).permission(true).build();

		Field userField = User.class.getDeclaredField("id");
		userField.setAccessible(true);
		userField.set(user,1L);
		userField.set(diffUser,2L);

		Field subscribeField = Subscribe.class.getDeclaredField("id");
		subscribeField.setAccessible(true);
		subscribeField.set(subscribe,10L);

	}
	@Test
	@DisplayName("팀 구독 성공")
	void subscribeTeam() {
		// given
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
		// when
		subscribeService.subscribeTeam(user, Team.T1);
		// then
		verify(subscribeRepository, times(1)).save(subscribeCaptor.capture());
		Subscribe res = subscribeCaptor.getValue();
		assertThat(res.getTeam()).isEqualTo(Team.T1);
		assertThat(res.getUser()).isEqualTo(user);
		assertThat(res.getUser().getSubscribedTeamCount()).isEqualTo(2);
		assertThat(res.getSubscriptionOrder()).isEqualTo(2);
	}

	@Test
	@DisplayName("팀 구독 실패 : 이미 구독한 팀")
	void subscribeTeamFailedSubscribeValidationException(){
		// given
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
		when(subscribeRepository.existsByUserAndTeam(user,Team.T1)).thenReturn(true);
		// when, then
		assertThatThrownBy(() -> subscribeService.subscribeTeam(user,Team.T1))
			.isInstanceOf(SubscribeValidationException.class)
			.hasFieldOrPropertyWithValue("error","이미 구독한 팀입니다. : "+Team.T1);
	}

	@Test
	@DisplayName("구독한 팀 목록 조회")
	void getSubscribedTeamList(){
		// given
		List<Subscribe> list = new ArrayList<>(50);
		for(int i=0; i<50; i++)
			list.add(Subscribe.builder()
				.user(user)
				.team(Team.T1)
				.subscribedDate(LocalDateTime.now())
				.permission(true)
				.subscriptionOrder(i)
				.build());
		when(subscribeRepository.findAllByUser(user)).thenReturn(list);
		// when
		List<GetSubscribeResponse> result = subscribeService.getSubscribedTeamList(user);
		// then
		for(int i=0; i<50; i++){
			assertThat(result.get(i).team()).isEqualTo(Team.T1.getName());
			assertThat(result.get(i).subscriptionOrder()).isEqualTo(i);
		}
	}

	@Test
	@DisplayName("팀 구독 취소")
	public void unsubscribeTeam(){
		// given
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
		when(subscribeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(subscribe));
		// when
		subscribeService.unsubscribeTeam(user,10L);
		// then
		verify(subscribeRepository,times(1)).delete(subscribe);
		assertThat(user.getSubscribedTeamCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("팀 구독 취소 실패 : 이미 구독하지 않은 팀")
	void unsubscribeTeamFailedSubscribeValidationException(){
		// given
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(subscribeRepository.findById(anyLong())).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> subscribeService.unsubscribeTeam(user,10L))
			.isInstanceOf(SubscribeValidationException.class)
			.hasFieldOrPropertyWithValue("error","이미 구독하지 않은 팀 입니다.");
	}

	@Test
	@DisplayName("팀 구독 취소 실패 : 구독 취소 권한 없음")
	void unsubscribeTeamFailedUserValidationException(){
		// given
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(diffUser));
		when(subscribeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(subscribe));
		// when, then
		assertThatThrownBy(() -> subscribeService.unsubscribeTeam(diffUser,10L))
			.isInstanceOf(UserValidationException.class)
			.hasFieldOrPropertyWithValue("error","구독 취소에 대한 권한이 없습니다.");
	}
}