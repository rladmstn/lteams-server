package com.lck.server.user.domain;

import java.time.LocalDate;

import com.lck.server.enumerate.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String email;
	private String password;
	private String nickname;
	private String profileImage;
	private Integer subscribedTeamCount;
	private LocalDate nicknameChangeableDate;
	private Role role;

	@Builder
	public User(String email, String password, String nickname, String profileImage, Integer subscribedTeamCount,
		LocalDate nicknameChangeableDate, Role role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.subscribedTeamCount = subscribedTeamCount;
		this.nicknameChangeableDate = nicknameChangeableDate;
		this.role = role;
	}
}
