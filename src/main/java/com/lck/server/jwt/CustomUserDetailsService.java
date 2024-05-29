package com.lck.server.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lck.server.user.exception.UserValidationException;
import com.lck.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UserValidationException("존재하지 않는 회원입니다."));
	}

	private UserDetails createUserDetails(com.lck.server.user.domain.User user){
		return User.builder()
			.username(user.getEmail())
			.password(user.getPassword())
			.roles(String.valueOf(new SimpleGrantedAuthority(user.getRole().toString())))
			.build();
	}
}
