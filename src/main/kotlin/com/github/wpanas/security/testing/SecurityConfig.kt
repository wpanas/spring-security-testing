package com.github.wpanas.security.testing

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {

	override fun configure(http: HttpSecurity?) {
		http?.let {
			it
					.csrf().disable()
					.authorizeRequests { authorizeRequests ->
						authorizeRequests.anyRequest()
								.authenticated()
					}
					.httpBasic()
		}
	}
}