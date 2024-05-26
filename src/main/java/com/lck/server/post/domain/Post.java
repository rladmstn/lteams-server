package com.lck.server.post.domain;

import java.time.LocalDateTime;

import com.lck.server.enumerate.Team;
import com.lck.server.user.domain.User;

import jakarta.persistence.Column;
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
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private Team team;
	private String title;
	@Column(columnDefinition = "TEXT")
	private String content;
	private Integer hitCount;
	private Integer recommendCount;
	private LocalDateTime createdTime;

	@Builder
	public Post(User user, Team team, String title, String content, Integer hitCount, Integer recommendCount,
		LocalDateTime createdTime) {
		this.user = user;
		this.team = team;
		this.title = title;
		this.content = content;
		this.hitCount = hitCount;
		this.recommendCount = recommendCount;
		this.createdTime = createdTime;
	}
}
