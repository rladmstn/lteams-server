package com.lck.server.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lck.server.enumerate.Team;
import com.lck.server.post.domain.Post;
import com.lck.server.post.domain.PostRecommendation;
import com.lck.server.post.dto.CreatePostRequest;
import com.lck.server.post.dto.EditPostRequest;
import com.lck.server.post.dto.GetPostResponse;
import com.lck.server.post.dto.PostRecommendationResponse;
import com.lck.server.post.exception.PostValidationException;
import com.lck.server.post.repository.PostRecommendationRepository;
import com.lck.server.post.repository.PostRepository;
import com.lck.server.user.domain.User;
import com.lck.server.user.exception.UserValidationException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final PostRecommendationRepository postRecommendationRepository;

	public void createPost(User user, CreatePostRequest request) {
		postRepository.save(request.toEntity(user));
		log.info("success to create post");
	}

	public List<GetPostResponse> getPostList(Team team) {
		List<GetPostResponse> result = postRepository.findAllByTeam(team);
		log.info("success to get post list");
		return result;
	}

	public void editPost(User user, EditPostRequest request){
		Post post = postRepository.findById(request.postId())
			.orElseThrow(() -> new PostValidationException("존재하지 않는 게시글 입니다."));
		checkUserPermission(user,post,"edit");

		if(request.title() != null && !request.title().isBlank())
			post.editTitle(request.title());
		if(request.content() != null && !request.content().isBlank())
			post.editContent(request.content());

		log.info("success to edit post");
	}

	public void updatePostHitCount(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostValidationException("존재하지 않는 게시글 입니다."));
		post.updateHitCount();
		log.info("success to update hit count");
	}

	public void deletePost(User user, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostValidationException("존재하지 않는 게시글 입니다."));
		checkUserPermission(user,post,"delete");
		postRepository.delete(post);
		log.info("success to delete post");
	}

	public PostRecommendationResponse updatePostRecommendation(User user, Long postId){
		// TODO : 팀 구독 여부 확인 로직 추가 구현 필요
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostValidationException("존재하지 않는 게시글 입니다."));
		Optional<PostRecommendation> recommend = postRecommendationRepository.findByUserAndPost(user, post);
		if(recommend.isPresent()){ // 추천 취소
			postRecommendationRepository.delete(recommend.get());
			post.updateRecommendCount(-1);
			log.info("success to cancel post recommendation");
			return new PostRecommendationResponse(postId, false);
		}
		else{ // 추천
			postRecommendationRepository.save(
				PostRecommendation.builder().user(user).post(post).build()
			);
			post.updateRecommendCount(1);
			log.info("success to update post recommendation");
			return new PostRecommendationResponse(postId, true);
		}
	}

	private void checkUserPermission(User user, Post post, String permission){
		if(!post.getUser().getId().equals(user.getId()))
			throw new UserValidationException("게시글에 대한 "+ permission +" 권한이 없습니다.");
	}
}
