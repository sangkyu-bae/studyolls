package com.studyolls.studyolls.infra.config;

import com.studyolls.studyolls.modules.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountService accountService;
    private final DataSource dataSource;

    /**
     * Spring Security 5.7.x 부터 WebSecurityConfigurerAdapter 는 Deprecated.
     * -> SecurityFilterChain, WebSecurityCustomizer 를 상황에 따라 빈으로 등록해 사용한다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email", "/check-email-token","/login-by-email",
                        "/email-login", "/check-email-login", "login-link", "/profile/*").permitAll()

                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .mvcMatchers("/favicon.ico", "/resources/**", "/error").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().logoutSuccessUrl("/")
//                .and()
//                .rememberMe().userDetailsService(accountService).tokenRepository(tokenRepository())
                .and().build();
    }


        @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository=new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;

    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
//    private final DataSource dataSource;
//    private final AccountService accountService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
//    /**
//     * Spring Security 5.7.x 부터 WebSecurityConfigurerAdapter 는 Deprecated.
//     * -> SecurityFilterChain, WebSecurityCustomizer 를 상황에 따라 빈으로 등록해 사용한다.
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.authorizeRequests()
//                .mvcMatchers("/", "/login", "/sign-up", "/check-email", "/check-email-token",
//                        "/email-login", "/check-email-login", "login-link", "/profile/*","/profile","/test").permitAll()
//                 .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
//                .mvcMatchers("/favicon.ico", "/resources/**", "/error").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().loginPage("/login").permitAll()
//                .and()
//                .logout().logoutSuccessUrl("/").permitAll()
//                .and()
//                .rememberMe().userDetailsService(accountService).tokenRepository(tokenRepository())
//                .and().build();
//    }
//
//    @Bean
//    public PersistentTokenRepository tokenRepository() {
//        JdbcTokenRepositoryImpl jdbcTokenRepository=new JdbcTokenRepositoryImpl();
//        jdbcTokenRepository.setDataSource(dataSource);
//        return jdbcTokenRepository;
//
//    }
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .mvcMatchers("/node_modules/**")
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }
}