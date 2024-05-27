package com.lck.server.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lck.server.enumerate.Role;
import com.lck.server.user.dto.RegisterRequest;
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
}
