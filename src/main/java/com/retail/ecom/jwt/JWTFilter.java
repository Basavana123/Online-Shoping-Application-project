package com.retail.ecom.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.retail.ecom.Exception.InvalidCredentialsException;
import com.retail.ecom.repository.AccessRepository;
import com.retail.ecom.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private AccessRepository accessRepository;
	private RefreshTokenRepository refreshRepository;
	private JWTService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String at = null;
		String rt = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("st"))
					at = cookie.getValue();
				if (cookie.getName().equals("rt"))
					rt = cookie.getValue();

			}
		}
		if (at != null && rt != null) {
			if (accessRepository.existsByTokenAndIsBlocked(at, true)
					&& refreshRepository.existsByTokenAndIsBlocked(rt, true))
				throw new InvalidCredentialsException("unAuthoried User");

			String username = jwtService.getUserName(at);
			String role = jwtService.getRole(rt);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {
				username = jwtService.getUserName(role);
				new UsernamePasswordAuthenticationToken(username, null,
						Collections.singleton(new SimpleGrantedAuthority(role)));
			}

		}
		filterChain.doFilter(request, response);

	}

}
