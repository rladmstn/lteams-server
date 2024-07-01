package com.lck.server.post.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lck.server.enumerate.Team;
import com.lck.server.post.dto.CreatePostRequest;
import com.lck.server.post.dto.GetPostResponse;
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
}
