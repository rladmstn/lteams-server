package com.lck.server.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lck.server.enumerate.Team;
import com.lck.server.post.domain.Post;
import com.lck.server.post.dto.GetPostResponse;

public interface PostRepository extends JpaRepository<Post,Long>{

	@Query("select new com.lck.server.post.dto.GetPostResponse("
		+ "p.id, p.title, p.user.nickname, p.content, p.hitCount, p.recommendCount, p.createdTime) "
		+ "from Post p "
		+ "join fetch User u "
		+ "on p.user = u "
		+ "where p.team = :team")
	List<GetPostResponse> findAllByTeam(Team team);

}
