package com.lck.server.post.service;

import org.springframework.stereotype.Service;

import com.lck.server.post.dto.CreatePostRequest;
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
}
