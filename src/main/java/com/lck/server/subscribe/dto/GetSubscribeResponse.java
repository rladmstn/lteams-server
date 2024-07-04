package com.lck.server.subscribe.dto;

import java.time.LocalDateTime;

import com.lck.server.subscribe.domain.Subscribe;

public record GetSubscribeResponse(Long id,
								   String team,
								   Integer subscriptionOrder,
								   Boolean permission,
								   LocalDateTime subscribedDate,
								   LocalDateTime permissionGrantedDate) {
	public static GetSubscribeResponse toDTO(Subscribe subscribe){
		return new GetSubscribeResponse(subscribe.getId(),
			subscribe.getTeam().getName(),
			subscribe.getSubscriptionOrder(),
			subscribe.getPermission(),
			subscribe.getSubscribedDate(),
			subscribe.getSubscribedDate().plusDays(subscribe.getSubscriptionOrder()*14)); // TODO : 기준 확립 필요
	}
}
