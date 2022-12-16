package com.example.nftmarket.Authentication;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.nftmarket.UserDetails.CustomUserDetailsService;
import com.example.nftmarket.UserDetails.UserServices;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserServices service;
    
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
     
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }	

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
         
        return authProvider;
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/h2-ui/**");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/test","/users","/createNFT","/wallet").authenticated()
            .antMatchers("/oauth/**").permitAll()
            .and()
            .formLogin()
                .usernameParameter("username")
                .defaultSuccessUrl("/test")
                .permitAll()
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .failureUrl("/login?error=true")
                .permitAll()
            .and()
            .csrf().disable()
            .oauth2Login()
            .loginPage("/login")
            .userInfoEndpoint()
                .userService(oauthUserService)
            .and()
            .successHandler(new AuthenticationSuccessHandler() {
         
                @Override
                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
         
                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
         
                    service.processOAuthPostLogin(oauthUser.getEmail());
         
                    response.sendRedirect("/test");
                }
            })
            .and()
            .csrf().disable()
            .logout().logoutSuccessUrl("/").permitAll();
            
    }

    @Autowired
    private CustomOAuth2UserService oauthUserService;
    

    
}