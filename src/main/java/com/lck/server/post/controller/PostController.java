package com.lck.server.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lck.server.common.ValidatedUser;
import com.lck.server.enumerate.Team;
import com.lck.server.exception.RequestException;
import com.lck.server.post.dto.CreatePostRequest;
import com.lck.server.post.dto.GetPostResponse;
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

	@GetMapping("/{team}")
	@Operation(summary = "팀 별 게시글 목록을 조회하는 API")
	public ResponseEntity<List<GetPostResponse>> getPostList(@PathVariable Team team){
		List<GetPostResponse> response = postService.getPostList(team);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{postId}")
	@Operation(summary = "게시글 상세 조회 시, 조회수 올리는 API")
	public ResponseEntity<Object> updatePostHitCount(@PathVariable Long postId){
		postService.updatePostHitCount(postId);
		return ResponseEntity.ok().body("OK");
	}

	@DeleteMapping
	@Operation(summary = "게시글 삭제 API")
	public ResponseEntity<Object> deletePost(@ValidatedUser User user, @RequestParam Long postId){
		postService.deletePost(user, postId);
		return ResponseEntity.ok().body("OK");
	}

}
