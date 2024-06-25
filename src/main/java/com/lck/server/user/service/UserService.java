package com.lck.server.user.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lck.server.enumerate.Role;
import com.lck.server.jwt.JwtDTO;
import com.lck.server.jwt.TokenProvider;
import com.lck.server.user.domain.User;
import com.lck.server.user.dto.RegisterRequest;
import com.lck.server.user.dto.SignInRequest;
import com.lck.server.user.dto.SignInResponse;
import com.lck.server.user.dto.UserInfoResponse;
import com.lck.server.user.exception.UserPermissionException;
import com.lck.server.user.exception.UserValidationException;
import com.lck.server.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authManagerBuilder;

	public void register(RegisterRequest request, MultipartFile profileImage) {
		checkEmailDuplicated(request.email());
		checkNicknameDuplicated(request.nickname());
		String imageUrl = imageService.saveImage(profileImage);
		String encodedPassword = passwordEncoder.encode(request.password());
		userRepository.save(request.toEntity(request.email(), encodedPassword, request.nickname(), imageUrl, Role.USER));
		log.info("success to register");
	}

	public void checkEmailDuplicated(String email){
		if(userRepository.existsByEmail(email))
			throw new UserValidationException("이미 가입된 이메일 입니다.");
	}

	public void checkNicknameDuplicated(String nickname){
		if(userRepository.existsByNickname(nickname))
			throw new UserValidationException("이미 존재하는 닉네임 입니다.");
	}

	public SignInResponse signIn(SignInRequest request){
		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(request.email(),request.password());
		Authentication authenticate = authManagerBuilder.getObject().authenticate(authenticationToken);

		JwtDTO token = tokenProvider.generateToken(authenticate);
		return new SignInResponse(token.getAccessToken());
	}

	public UserInfoResponse getUserInfo(User user) {
		return new UserInfoResponse(user.getEmail(), user.getNickname(), user.getProfileImage());
	}

	public void editUserInfo(User user, String nickname, MultipartFile profileImage) {
		User targetUser = userRepository.findById(user.getId()).orElseThrow();
		if(nickname != null && !nickname.isBlank())
			targetUser.editNickname(nickname);
		if(profileImage != null){
			if(targetUser.getProfileImage() != null)
				imageService.deleteImage(targetUser.getProfileImage());
			String imageUrl = imageService.saveImage(profileImage);
			targetUser.editProfileImage(imageUrl);
		}
		log.info("success to edit user info");
	}

	public void unregisterUser(User user, String password) {
		checkPassword(user.getPassword(), password);
		userRepository.delete(user);
		log.info("success to unregister user");
	}

	private void checkPassword(String userPassword, String inputPassword){
		if(!passwordEncoder.matches(inputPassword, userPassword))
			throw new UserPermissionException("비밀번호가 일치하지 않습니다.");
	}
}
