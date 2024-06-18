package com.lck.server.user.service;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.lck.server.user.domain.User;
import com.lck.server.user.dto.RegisterRequest;
import com.lck.server.user.dto.UserInfoResponse;
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
	void editUserInfo(){
		// given
		// when
		// then
	}

}