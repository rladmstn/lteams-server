package com.lck.server.common;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.lck.server.jwt.TokenProvider;
import com.lck.server.user.domain.User;
import com.lck.server.user.exception.UserValidationException;
import com.lck.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidatedUserResolver implements HandlerMethodArgumentResolver {

	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(ValidatedUser.class)
			&& User.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String jwt = webRequest.getHeader("Authorization");
		if (jwt != null)
			return userRepository.findByEmail(tokenProvider.getUerEmail(jwt)).orElseThrow(() -> new RuntimeException("없는 사용자입니다."));
		else
			throw new UserValidationException("헤더에 인증 정보가 포함되어있지 않습니다.");
	}
}
