package com.vinaction.api.config;

import com.vinaction.api.jwt.JwtAccessDeniedHandler;
import com.vinaction.api.jwt.JwtAuthenticationEntryPoint;
import com.vinaction.api.jwt.JwtSecurityConfig;
import com.vinaction.api.jwt.TokenProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsUtils;

@EnableWebSecurity // 웹 보안 활성화 어노테이션
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // 이후에 메소드 위 어노테이션으로 권한제한 하기 위해서 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring()
//                .antMatchers(
//                        "/h2-console/**"
//                        ,"/favicon.ico"
//                        ,"/error"
//                );
//    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.\
                .cors().and()
                .csrf().disable()

//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

//                // enable h2-console
//                .and()
//                .headers()
//                .frameOptions()
//                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                .antMatchers("/api/signUp").permitAll()
//                .antMatchers("/api/authenticate").permitAll()
//                .antMatchers(HttpMethod.GET,"/api/boards").permitAll()
//                .antMatchers(HttpMethod.GET,"/api/board/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/img/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/").permitAll()
                .antMatchers(HttpMethod.GET, "/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/").permitAll()
//                .antMatchers("/api/post/**").permitAll()

                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
