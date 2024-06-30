package com.lck.server.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lck.server.enumerate.Role;
import com.lck.server.enumerate.Team;
import com.lck.server.post.domain.Post;
import com.lck.server.post.dto.CreatePostRequest;
import com.lck.server.post.repository.PostRepository;
import com.lck.server.user.domain.User;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;

	private User user;
	private CreatePostRequest createPostRequest;
	@Captor
	private ArgumentCaptor<Post> postCaptor;
	@BeforeEach
	void setUp() {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("profileImage").subscribedTeamCount(0)
			.nicknameChangeableDate(LocalDate.now()).role(Role.USER).build();
		createPostRequest = new CreatePostRequest(Team.T1, "title", "content");
	}

	@Test
	@DisplayName("게시글 작성")
	void createPost() {
		// given
		// when
		postService.createPost(user, createPostRequest);
		// then
		verify(postRepository, times(1)).save(postCaptor.capture());
		Post post = postCaptor.getValue();
		assertThat(post.getUser()).isEqualTo(user);
		assertThat(post.getTitle()).isEqualTo("title");
		assertThat(post.getTeam()).isEqualTo(Team.T1);
		assertThat(post.getContent()).isEqualTo("content");

	}
}