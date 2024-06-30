package com.lck.server.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lck.server.common.ValidatedUser;
import com.lck.server.exception.RequestException;
import com.lck.server.post.dto.CreatePostRequest;
import com.lck.server.post.service.PostService;
import com.lck.server.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 관련 API")
@RequestMapping("/api/post")
public class PostController {
	private final PostService postService;

	@PostMapping
	@Operation(summary = "게시글 작성 시 사용하는 API")
	public ResponseEntity<Object> createPost(@ValidatedUser User user, @Valid @RequestBody CreatePostRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("게시글 작성 요청이 올바르지 않습니다.",errors);
		postService.createPost(user, request);
		return ResponseEntity.ok().body("OK");
	}

}
