package com.trism.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.extern.java.Log;

@Log
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	DataSource dataSource;

	@Autowired
	TrismUsersService trismUserService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		log.info("security config..............");

		
		http.authorizeRequests()
		.antMatchers("/boards/list").permitAll()
		.antMatchers("/boards/register")
		.hasAnyRole("BASIC", "MANAGER", "ADMIN");
		
		// http.formLogin();
		http.formLogin()
		.loginPage("/login").successHandler(new LoginSuccessHandler());
		
		
		http.exceptionHandling().accessDeniedPage("/accessDenied");
		
		// http.logout().invalidateHttpSession(true);
		http.logout().logoutUrl("/logout").invalidateHttpSession(true);

		// http.userDetailsService(zerockUserService);

		http.rememberMe()
		    .key("zerock")
		    .userDetailsService(trismUserService)
		    .tokenRepository(getJDBCRepository())
			.tokenValiditySeconds(60 * 60 * 24);
		
	}

	private PersistentTokenRepository getJDBCRepository() {

		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}
	


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		log.info("build Auth global........");

		auth.userDetailsService(trismUserService).passwordEncoder(passwordEncoder());

	}	
}