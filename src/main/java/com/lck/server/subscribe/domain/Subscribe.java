package com.lck.server.subscribe.domain;

import java.time.LocalDateTime;

import com.lck.server.enumerate.Team;
import com.lck.server.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Subscribe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private LocalDateTime subscribedDate;
	private Boolean permission;
	private Integer subscriptionOrder;
	private Team team;

	@Builder
	public Subscribe(User user, LocalDateTime subscribedDate, Boolean permission, Integer subscriptionOrder,
		Team team) {
		this.user = user;
		this.subscribedDate = subscribedDate;
		this.permission = permission;
		this.subscriptionOrder = subscriptionOrder;
		this.team = team;
	}

	public void updateSubscriptionOrder(){
		this.subscriptionOrder--;
	}
}
