package com.codemyth.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.codemyth.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final JwtProperties jwtProperties;
	private final SecretKey signingKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
	}

	public String generateToken(String username, Collection<String> roles) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.expirationMs());
		return Jwts.builder().subject(username).claim("roles", List.copyOf(roles)).issuedAt(now).expiration(expiry)
				.signWith(signingKey).compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
		Object roles = parseClaims(token).get("roles");
		if (roles instanceof List<?> list) {
			return list.stream().map(Object::toString).toList();
		}
		return Collections.emptyList();
	}

	public boolean isValid(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		}
		catch (RuntimeException ex) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
	}
}
