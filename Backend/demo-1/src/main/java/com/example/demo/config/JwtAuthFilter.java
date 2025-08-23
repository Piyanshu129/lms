package com.example.demo.config;


import com.example.demo.service.JwtService;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends jakarta.servlet.GenericFilter {

private final JwtService jwtService;
private final UserRepository userRepository;

public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
this.jwtService = jwtService;
this.userRepository = userRepository;
}

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
throws IOException, ServletException {
	HttpServletRequest http = (HttpServletRequest) request;
	String header = http.getHeader("Authorization");
    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
    	  String token = header.substring(7);
    	  try {
    	    var jws = jwtService.parse(token);          // parse and validate JWT
    	    String email = jws.getBody().getSubject();  // subject should be the email/username

    	    // Load your domain User and set it as the principal
    	    Optional<User> userOpt = userRepository.findByEmail(email);
    	    if (userOpt.isPresent()) {
    	      User user = userOpt.get();
    	      var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

    	      Authentication authentication =
    	          new UsernamePasswordAuthenticationToken(user, null, authorities);

    	      SecurityContextHolder.getContext().setAuthentication(authentication);
    	    }
    	    // If user not found, leave context unauthenticated and let security handle 401 for protected routes
    	  } catch (Exception ignored) {
    	    // token parse/validation errors -> leave unauthenticated
    	  }
    	}

    	chain.doFilter(request, response);

  }
}
