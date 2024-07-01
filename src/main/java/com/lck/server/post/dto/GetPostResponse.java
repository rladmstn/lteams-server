package com.lck.server.post.dto;

import java.time.LocalDateTime;

public record GetPostResponse(Long id,
							  String title,
							  String writer,
							  String content,
							  Integer hitCount,
							  Integer recommendCount,
							  LocalDateTime createdTime) {
}
