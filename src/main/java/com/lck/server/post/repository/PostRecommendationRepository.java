package com.lck.server.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lck.server.post.domain.Post;
import com.lck.server.post.domain.PostRecommendation;
import com.lck.server.user.domain.User;

public interface PostRecommendationRepository extends JpaRepository<PostRecommendation, Long> {
	Optional<PostRecommendation> findByUserAndPost(User user, Post post);
}
