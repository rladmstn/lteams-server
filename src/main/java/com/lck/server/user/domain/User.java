package com.lck.server.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;

import com.lck.server.enumerate.Role;

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
@SQLDelete(sql = "update User set deleted_at = current_timestamp where id = ?")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String nickname;
	private String profileImage;
	private Integer subscribedTeamCount;
	private LocalDate nicknameChangeableDate;
	private Role role;
	private LocalDateTime deletedAt;

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
		this.deletedAt = null;
	}

	public void editNickname(String nickname){
		this.nickname = nickname;
	}
	public void editProfileImage(String profileImage){
		this.profileImage = profileImage;
	}
	public void updateSubscribedTeamCount(Integer subscribedTeamCount){ this.subscribedTeamCount = subscribedTeamCount; }
}
