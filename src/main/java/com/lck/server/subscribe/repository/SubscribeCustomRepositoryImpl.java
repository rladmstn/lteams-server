package com.lck.server.subscribe.repository;

import static com.lck.server.subscribe.domain.QSubscribe.*;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.lck.server.subscribe.domain.Subscribe;
import com.lck.server.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class SubscribeCustomRepositoryImpl implements SubscribeCustomRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Subscribe> findListGreaterThanOrder(User user, Integer order) {
		return queryFactory.selectFrom(subscribe)
			.where(subscribe.user.eq(user)
				.and(subscribe.subscriptionOrder.gt(order)))
			.fetch();
	}
}
