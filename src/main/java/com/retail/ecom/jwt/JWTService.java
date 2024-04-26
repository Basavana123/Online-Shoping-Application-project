package com.retail.ecom.jwt;

import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.retail.ecom.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Maps;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	
	@Value("${myapp.jwt.secret}")
	private String secret;
	
	@Value("${myapp.jwt.access.expiration}")
	private long accessExpiry;
	
	@Value("${myapp.jwt.refresh.expiration}")
	private long refreshExpiry;
	
	public String genrateAccessToken(User user,String role) {
		return generateToken(user, accessExpiry, role);
	}
	
	public String genrateRefeshToken(User user,String role) {
		return generateToken(user, refreshExpiry,role);  
	}
	  
	private String generateToken(User user, long expiration,String role) {
		return Jwts.builder()
		.setClaims(Maps.of("role", role).build())
		.setSubject(user.getUserName())
		.setIssuedAt(new Date(System.currentTimeMillis()))
		.setExpiration(new Date(System.currentTimeMillis()+expiration))
		.signWith(getSignatureKey(),SignatureAlgorithm.HS256)
		.compact();
	}
	
	private Key getSignatureKey() {
		
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		
	}
	
	public String getUserName(String token) {
		return claims(token).getSubject();
	}
	
	private Claims claims(String JwtToken) {
		return Jwts.parserBuilder().setSigningKey(getSignatureKey()).build().parseClaimsJws(JwtToken).getBody();
	}

	public String getRole(String token) {
		
		return claims(token).getSubject();
	}
	
	 
	

}
