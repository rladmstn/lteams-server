package com.lck.server.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lck.server.post.domain.Post;

public interface PostRepository extends JpaRepository<Post,Long> {
}
