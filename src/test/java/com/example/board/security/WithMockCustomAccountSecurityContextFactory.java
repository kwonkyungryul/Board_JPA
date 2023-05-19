/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.board.security;



import com.example.board.auth.session.MyUserDetails;
import com.example.board.consts.UserConst;
import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.enums.UserStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


import java.util.List;

public class WithMockCustomAccountSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser mockCustomUser) {
		var user = UserConst.user;
		var myUserDetails = new MyUserDetails(user);
		var securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(
				new UsernamePasswordAuthenticationToken(
						myUserDetails,
						myUserDetails.getPassword(),
						myUserDetails.getAuthorities()
				)
		);
		return securityContext;
	}

}
