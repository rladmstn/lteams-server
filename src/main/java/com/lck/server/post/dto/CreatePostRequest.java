package com.lck.server.post.dto;

import java.time.LocalDateTime;

import com.lck.server.enumerate.Team;
import com.lck.server.post.domain.Post;
import com.lck.server.user.domain.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePostRequest(@NotNull(message = "팀 선택이 필요합니다.") Team team,
								@NotBlank(message = "게시글 제목 작성이 필요합니다.") String title,
								@NotBlank(message = "게시글 본문 작성이 필요합니다.") String content) {
	public Post toEntity(User user){
		return Post.builder()
			.user(user)
			.team(team)
			.title(title)
			.content(content)
			.hitCount(0)
			.recommendCount(0)
			.createdTime(LocalDateTime.now())
			.build();
	}
}
