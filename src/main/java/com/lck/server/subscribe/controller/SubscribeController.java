package com.lck.server.subscribe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lck.server.common.ValidatedUser;
import com.lck.server.enumerate.Team;
import com.lck.server.subscribe.dto.GetSubscribeResponse;
import com.lck.server.subscribe.service.SubscribeService;
import com.lck.server.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "팀 구독 API", description = "팀 구독 관련 API")
@RequestMapping("/api/subscribe")
public class SubscribeController {
	private final SubscribeService subscribeService;

	@PostMapping
	@Operation(summary = "팀 구독 API")
	public ResponseEntity<Object> subscribeTeam(@ValidatedUser User user, @RequestParam Team team){
		subscribeService.subscribeTeam(user, team);
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping
	@Operation(summary = "구독한 팀 목록 조회 API")
	public ResponseEntity<List<GetSubscribeResponse>> getSubscribedTeamList(@ValidatedUser User user){
		List<GetSubscribeResponse> response = subscribeService.getSubscribedTeamList(user);
		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping
	@Operation(summary = "팀 구독 취소 API")
	public ResponseEntity<Object> unsubscribeTeam(@ValidatedUser User user, @RequestParam Long subscribeId){
		subscribeService.unsubscribeTeam(user, subscribeId);
		return ResponseEntity.ok().body("OK");
	}

}
