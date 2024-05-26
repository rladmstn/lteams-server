package com.lck.server.chat.domain;

import java.time.LocalDateTime;

import com.lck.server.enumerate.Team;
import com.lck.server.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	private Team team;

	private String content;
	private LocalDateTime spentTime;
	private Integer reportedCount;

	@Builder
	public Chat(User user, Team team, String content, LocalDateTime spentTime, Integer reportedCount) {
		this.user = user;
		this.team = team;
		this.content = content;
		this.spentTime = spentTime;
		this.reportedCount = reportedCount;
	}
}
