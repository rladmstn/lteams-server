package com.lck.server.user.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lck.server.common.ValidatedUser;
import com.lck.server.exception.RequestException;
import com.lck.server.user.domain.User;
import com.lck.server.user.dto.RegisterRequest;
import com.lck.server.user.dto.SignInRequest;
import com.lck.server.user.dto.SignInResponse;
import com.lck.server.user.dto.UserInfoResponse;
import com.lck.server.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 관련 API")
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "회원가입 시 사용하는 API")
	public ResponseEntity<Object> register(@Valid @RequestPart RegisterRequest request, Errors errors,
		@RequestPart(required = false) MultipartFile profileImage){
		if(errors.hasErrors())
			throw new RequestException("회원가입 요청이 올바르지 않습니다.",errors);

		userService.register(request,profileImage);
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping(value = "/check-email")
	@Operation(summary = "이메일 중복 확인 API")
	public ResponseEntity<Object> checkEmail(@RequestParam String email){
		userService.checkEmailDuplicated(email);
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping(value = "/check-nickname")
	@Operation(summary = "닉네임 중복 확인 API")
	public ResponseEntity<Object> checkPassword(@RequestParam String nickname){
		userService.checkNicknameDuplicated(nickname);
		return ResponseEntity.ok().body("OK");
	}

	@PostMapping(value = "/sign-in")
	@Operation(summary = "로그인 API", description = "이메일, 비밀번호로 로그인하는 API")
	public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("로그인 요청이 올바르지 않습니다.",errors);
		SignInResponse response = userService.signIn(request);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping
	@Operation(summary = "회원 정보 조회 API", description = "회원 이메일, 닉네임, 프로필 이미지 정보를 조회하는 API")
	public ResponseEntity<UserInfoResponse> getUserInfo(@ValidatedUser User user){
		UserInfoResponse response = userService.getUserInfo(user);
		return ResponseEntity.ok().body(response);
	}

	@PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "회원 정보 수정 API", description = "닉네임, 프로필 이미지를 수정하는 API")
	public ResponseEntity<Object> editUserInfo(@ValidatedUser User user,
		@RequestParam(required = false) String nickname, @RequestPart(required = false) MultipartFile profileImage){
		userService.editUserInfo(user, nickname, profileImage);
		return ResponseEntity.ok().body("OK");
	}

	@DeleteMapping
	@Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴하는 API")
	public ResponseEntity<Object> unregisterUser(@ValidatedUser User user, @RequestParam String password){
		userService.unregisterUser(user, password);
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping(value = "/test")
	@Operation(summary = "테스트 API")
	public ResponseEntity<Object> test(@ValidatedUser User user){
		return ResponseEntity.ok().body(user.getEmail());
	}
}
