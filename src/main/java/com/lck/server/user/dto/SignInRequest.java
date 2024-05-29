package com.lck.server.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank(message = "이메일은 필수 입력입니다.") String email,
							@NotBlank(message = "비밀번호는 필수 입력입니다.") String password) {
}
