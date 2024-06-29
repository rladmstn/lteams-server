package com.lck.server.user.repository;

import static com.lck.server.user.domain.QUser.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.lck.server.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsByEmail(String email) {
		return queryFactory.selectFrom(user)
			.where(user.email.eq(email)
				.and(user.deletedAt.isNull()))
			.fetchOne() != null;
	}

	@Override
	public boolean existsByNickname(String nickname){
		return queryFactory.selectFrom(user)
			.where(user.nickname.eq(nickname)
				.and(user.deletedAt.isNull()))
			.fetchOne() != null;
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return Optional.ofNullable(queryFactory.selectFrom(user)
			.where(user.email.eq(email)
				.and(user.deletedAt.isNull()))
			.fetchOne());
	}

}
