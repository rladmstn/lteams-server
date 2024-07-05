package com.lck.server.subscribe.repository;

import java.util.List;

import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.user.domain.User;

public interface SubscribeCustomRepository {
	List<Subscribe> findListGreaterThanOrder(User user, Integer order);
}
