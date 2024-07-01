package com.lck.server.post.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lck.server.enumerate.Team;
import com.lck.server.post.domain.Post;
import com.lck.server.post.dto.CreatePostRequest;
import com.lck.server.post.dto.GetPostResponse;
import com.lck.server.post.exception.PostValidationException;
import com.lck.server.post.repository.PostRepository;
import com.lck.server.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;

	public void createPost(User user, CreatePostRequest request) {
		postRepository.save(request.toEntity(user));
		log.info("success to create post");
	}

	public List<GetPostResponse> getPostList(Team team) {
		List<GetPostResponse> result = postRepository.findAllByTeam(team);
		log.info("success to get post list");
		return result;
	}

	public void updatePostHitCount(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostValidationException("존재하지 않는 게시글 입니다."));
		post.updateHitCount();
		log.info("success to update hit count");
	}
}
