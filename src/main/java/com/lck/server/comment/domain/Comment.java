package com.lck.server.comment.domain;

import java.time.LocalDateTime;

import com.lck.server.post.domain.Post;
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
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(columnDefinition = "TEXT")
	private String content;
	private Integer recommendedCount;
	private LocalDateTime createdTime;

	@Builder
	public Comment(User user, Post post, String content, Integer recommendedCount, LocalDateTime createdTime) {
		this.user = user;
		this.post = post;
		this.content = content;
		this.recommendedCount = recommendedCount;
		this.createdTime = createdTime;
	}
}
