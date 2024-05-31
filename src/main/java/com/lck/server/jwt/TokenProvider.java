package com.lck.server.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenProvider {
	private final Key key;
	@Value("${jwt_expiration_time}")
	private long accessTokenExpiration;

	public TokenProvider(@Value("${jwt_secret_key}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public JwtDTO generateToken(Authentication authentication){
		// get authorities
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();

		// create access token
		Date accessTokenExpireDate = new Date(now + accessTokenExpiration);
		String accessToken = Jwts.builder()
			.setSubject(authentication.getName())
			.claim("auth", authorities)
			.setExpiration(accessTokenExpireDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		return JwtDTO.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.build();
	}

	public Authentication getAuthentication(String accessToken){
		// Jwt token decode
		Claims claims = parseClaims(accessToken);
		if (claims.get("auth") == null)
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		
		// get authentication info from claims
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
			.map(SimpleGrantedAuthority::new)
			.toList();

		UserDetails principal = new User(claims.getSubject(),"",authorities);
		return new UsernamePasswordAuthenticationToken(principal,"",authorities);
	}

	public boolean validateToken(String token){
		// TODO : 예외처리 필요
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e){
			throw new RuntimeException("검증되지 않은 토큰입니다.");
		} catch (ExpiredJwtException e){
			throw new RuntimeException("만료된 토큰입니다.");
		} catch (UnsupportedJwtException e){
			throw new RuntimeException("지원하지 않는 형태의 토큰입니다.");
		} catch (IllegalArgumentException e){
			throw new RuntimeException("토큰 정보가 비어있습니다.");
		}
	}

	private Claims parseClaims(String accessToken){
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(accessToken)
			.getBody();
	}

	public String getUerEmail(String authToken){
		String token = authToken.replace("Bearer","").trim();
		Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
		return claimsJws.getBody().getSubject();
	}

}
