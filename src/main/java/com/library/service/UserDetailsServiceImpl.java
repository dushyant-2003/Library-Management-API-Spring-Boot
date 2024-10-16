package com.library.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.library.dao.UserDAO;
import com.library.model.User;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userRes = Optional.ofNullable(userDAO.getUserByUserName(username));
		if (userRes.isEmpty())
			throw new UsernameNotFoundException("Could not findUser with username = " + username);
		User user = userRes.get();
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
	}
}
