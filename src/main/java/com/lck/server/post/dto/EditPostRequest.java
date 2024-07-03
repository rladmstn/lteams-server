package com.lck.server.post.dto;

import jakarta.validation.constraints.NotNull;

public record EditPostRequest(@NotNull(message = "게시글 고유 아이디는 필수 입력입니다.") Long postId,
							  String title,
							  String content) {
}
