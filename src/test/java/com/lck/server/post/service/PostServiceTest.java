package com.lck.server.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.lck.server.post.dto.EditPostRequest;
import com.lck.server.post.dto.GetPostResponse;
import com.lck.server.post.exception.PostValidationException;
import com.lck.server.post.repository.PostRepository;
import com.lck.server.user.domain.User;
import com.lck.server.user.exception.UserValidationException;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;
	private User user;
	private User diffUser;
	private Post post;
	@Captor
	private ArgumentCaptor<Post> postCaptor;
	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("profileImage").subscribedTeamCount(0)
			.nicknameChangeableDate(LocalDate.now()).role(Role.USER).build();

		diffUser = User.builder().email("diffEmail").password("password").nickname("nickname").profileImage("profileImage").subscribedTeamCount(0)
			.nicknameChangeableDate(LocalDate.now()).role(Role.USER).build();

		Field userField = User.class.getDeclaredField("id");
		userField.setAccessible(true);
		userField.set(user,10L);
		userField.set(diffUser,20L);

		post = Post.builder().team(Team.T1).title("title").content("content").user(user).recommendCount(0).hitCount(0).createdTime(LocalDateTime.now()).build();

		Field postField = Post.class.getDeclaredField("id");
		postField.setAccessible(true);
		postField.set(post,1L);
	}

	@Test
	@DisplayName("게시글 작성")
	void createPost() {
		// given
		CreatePostRequest createPostRequest = new CreatePostRequest(Team.T1, "title", "content");
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

	@Test
	@DisplayName("게시글 목록 조회")
	void getPostList(){
		// given
		List<GetPostResponse> list = new ArrayList<>(3);
		for(int i = 0; i<50; i++)
			 list.add(new GetPostResponse((long)i,"title"+i,"nickname"+i,"content"+i,i,i, LocalDateTime.now().minusDays(i)));
		when(postRepository.findAllByTeam(Team.T1)).thenReturn(list);
		// when
		List<GetPostResponse> result = postService.getPostList(Team.T1);
		// then
		assertThat(result.size()).isEqualTo(50);
		for(int i=0; i<50; i++){
			assertThat(result.get(i).id()).isEqualTo(i);
			assertThat(result.get(i).title()).isEqualTo("title"+i);
			assertThat(result.get(i).content()).isEqualTo("content"+i);
		}
	}

	@Test
	@DisplayName("게시글 편집 성공")
	void editPost(){
		// given
		EditPostRequest request = new EditPostRequest(1L, "editTitle","editContent");
		when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
		// when
		postService.editPost(user,request);
		// then
		assertThat(post.getTitle()).isEqualTo("editTitle");
		assertThat(post.getContent()).isEqualTo("editContent");
	}

	@Test
	@DisplayName("게시글 편집 실패 : 존재하지 않는 게시글")
	void editPostFailedPostValidationException(){
		// given
		EditPostRequest request = new EditPostRequest(2L, "editTitle","editContent");
		when(postRepository.findById(2L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> postService.editPost(user,request))
			.isInstanceOf(PostValidationException.class)
			.hasFieldOrPropertyWithValue("error","존재하지 않는 게시글 입니다.");
	}

	@Test
	@DisplayName("게시글 편집 실패 : 사용자 권한 없음")
	void editPostFailedUserValidationException(){
		// given
		EditPostRequest request = new EditPostRequest(2L, "editTitle","editContent");
		when(postRepository.findById(2L)).thenReturn(Optional.ofNullable(post));
		// when, then
		assertThatThrownBy(() -> postService.editPost(diffUser,request))
			.isInstanceOf(UserValidationException.class)
			.hasFieldOrPropertyWithValue("error","게시글에 대한 edit 권한이 없습니다.");
	}

	@Test
	@DisplayName("게시글 삭제 성공")
	void deletePost(){
		// given
		when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));
		// when
		postService.deletePost(user, 1L);
		// then
		verify(postRepository,times(1)).delete(post);
	}

	@Test
	@DisplayName("게시글 삭제 실패 : 존재하지 않는 게시글")
	void deletePostFailedPostValidationException(){
		// given
		when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> postService.deletePost(user, 1L))
			.isInstanceOf(PostValidationException.class)
			.hasFieldOrPropertyWithValue("error","존재하지 않는 게시글 입니다.");
	}

	@Test
	@DisplayName("게시글 삭제 실패 : 사용자 권한 없음")
	void deletePostFailedUserValidationException(){
		// given
		when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));
		// when, then
		assertThatThrownBy(() -> postService.deletePost(diffUser, 1L))
			.isInstanceOf(UserValidationException.class)
			.hasFieldOrPropertyWithValue("error","게시글에 대한 delete 권한이 없습니다.");
	}

	@Test
	@DisplayName("게시글 조회수 업데이트")
	void updatePostHitCount(){
		// given
		when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));
		// when
		postService.updatePostHitCount(1L);
		// then
		assertThat(post.getHitCount()).isEqualTo(1);
	}


}