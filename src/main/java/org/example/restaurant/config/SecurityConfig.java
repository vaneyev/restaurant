package org.example.restaurant.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableCaching
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/restaurants").hasRole("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/restaurants").hasRole("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/restaurants/**").hasRole("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/menus").hasRole("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/menus").hasRole("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/menus/**").hasRole("ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        http.httpBasic();
        http.csrf().disable();
    }
}
