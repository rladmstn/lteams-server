package com.lck.server.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Objects;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.lck.server.user.domain.User;
import com.lck.server.user.dto.RegisterRequest;
import com.lck.server.user.dto.UserInfoResponse;
import com.lck.server.user.exception.UserPermissionException;
import com.lck.server.user.exception.UserValidationException;
import com.lck.server.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService userService;
	@Mock
	private ImageService imageService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	private RegisterRequest registerRequest;
	@Captor
	private ArgumentCaptor<User> userCaptor;
	@BeforeEach
	void set(){
		registerRequest = new RegisterRequest("email","password", "nickname");
	}

	@Test
	@DisplayName("회원가입 성공")
	void register() {
		// given
		MockMultipartFile profileImage = new MockMultipartFile("profileImage",new byte[]{1,2,3});
		when(passwordEncoder.encode(registerRequest.password())).thenReturn("encoded");
		when(imageService.saveImage(any(MultipartFile.class))).thenReturn("imageUrl");
		// when
		userService.register(registerRequest,profileImage);
		// then
		verify(userRepository,times(1)).save(userCaptor.capture());
		User user = userCaptor.getValue();
		assertThat(user.getEmail()).isEqualTo("email");
		assertThat(user.getPassword()).isEqualTo("encoded");
		assertThat(user.getNickname()).isEqualTo("nickname");
		assertThat(user.getProfileImage()).isEqualTo("imageUrl");
	}

	@Test
	@DisplayName("회원가입 실패 : 이메일 중복")
	void registerFailedEmailDuplicated(){
		// given
		MockMultipartFile profileImage = new MockMultipartFile("profileImage",new byte[]{1,2,3});
		when(userRepository.existsByEmail("email")).thenReturn(true);
		// when, then
		assertThatThrownBy(() -> userService.register(registerRequest,profileImage))
			.isInstanceOf(UserValidationException.class)
			.hasFieldOrPropertyWithValue("error", "이미 가입된 이메일 입니다.");
	}
	@Test
	@DisplayName("회원가입 실패 : 닉네임 중복")
	void registerFailedNicknameDuplicated(){
		// given
		MockMultipartFile profileImage = new MockMultipartFile("profileImage",new byte[]{1,2,3});
		when(userRepository.existsByNickname("nickname")).thenReturn(true);
		// when, then
		assertThatThrownBy(() -> userService.register(registerRequest,profileImage))
			.isInstanceOf(UserValidationException.class)
			.hasFieldOrPropertyWithValue("error", "이미 존재하는 닉네임 입니다.");
	}

	@Test
	@DisplayName("회원 정보 조회 성공")
	void getUserInfo(){
		// given
		User user = User.builder().email("email").nickname("nickname").password("encoded").profileImage("profileImage")
			.nicknameChangeableDate(LocalDate.now()).subscribedTeamCount(0).build();
		// when
		UserInfoResponse response = userService.getUserInfo(user);
		// then
		assertThat(response.email()).isEqualTo("email");
		assertThat(response.nickname()).isEqualTo("nickname");
		assertThat(response.profileImage()).isEqualTo("profileImage");
	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	void editUserInfo() throws NoSuchFieldException, IllegalAccessException {
		// given
		User user = User.builder().email("email").nickname("nickname").password("encoded").profileImage("profileImage")
			.nicknameChangeableDate(LocalDate.now()).subscribedTeamCount(0).build();

		Field userIdField = User.class.getDeclaredField("id");
		userIdField.setAccessible(true);
		userIdField.set(user,123L);

		MockMultipartFile newProfileImage = new MockMultipartFile("newProfileImage",new byte[]{1,2,3,4});
		when(imageService.saveImage(newProfileImage)).thenReturn("newProfileImage");
		when(userRepository.findById(123L)).thenReturn(Optional.ofNullable(user));
		// when
		userService.editUserInfo(Objects.requireNonNull(user),"newNickname",newProfileImage);
		// then
		assertThat(user.getProfileImage()).isEqualTo("newProfileImage");
		assertThat(user.getNickname()).isEqualTo("newNickname");
	}

	@Test
	@DisplayName("회원 탈퇴 성공")
	void unregisterUser(){
		// given
		User user = User.builder().email("email").nickname("nickname").password("encoded").profileImage("profileImage")
			.nicknameChangeableDate(LocalDate.now()).subscribedTeamCount(0).build();
		when(passwordEncoder.matches("password",user.getPassword())).thenReturn(true);
		// when
		userService.unregisterUser(user,"password");
		// then
		verify(userRepository,times(1)).delete(user);
	}

	@Test
	@DisplayName("회원 탈퇴 실패 : 비밀번호 불일치")
	void unregisterUserFailedIncorrectPassword(){
		User user = User.builder().email("email").nickname("nickname").password("encoded").profileImage("profileImage")
			.nicknameChangeableDate(LocalDate.now()).subscribedTeamCount(0).build();
		when(passwordEncoder.matches("incorrectPassword",user.getPassword())).thenReturn(false);
		// when
		assertThatThrownBy(() -> userService.unregisterUser(user,"incorrectPassword"))
			.isInstanceOf(UserPermissionException.class)
			.hasFieldOrPropertyWithValue("error","비밀번호가 일치하지 않습니다.");

	}
}