package com.config;


import com.filter.JwtAuthorizeizeFilter;
import com.user.model.RestBean;
import com.user.model.dto.UserEntity;
import com.user.model.vo.response.AuthorizeVO;

import com.user.repository.UserRepository;
import com.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig {

    @Resource
    JwtUtils utils;

    @Resource
    JwtAuthorizeizeFilter jwtAuthorizeizeFilter;

    @Resource
    UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // requestMatchers 指向路徑 permitAll 不需登入  authenticated 需要登入
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**").permitAll() // api/auth/ 開頭都不用登入
                        .anyRequest().authenticated() // 未指名頁面都需要登入
                )
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)

                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }




    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");

        // 獲取已驗證的用戶
        User user = (User) authentication.getPrincipal();

        // 查找用戶資料，使用新的方法名
        UserEntity userEntity = userRepository.findByAccountOrEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("用戶名或郵件錯誤"));

        // 創建 JWT
        String token = utils.createJwt(user, userEntity.getUserId(), userEntity.getAccount());

        // 準備返回的數據
        AuthorizeVO vo = new AuthorizeVO();
        vo.setExpire(utils.expireTime());
        vo.setRole(userEntity.getRole());
        vo.setToken(token);
        vo.setUsername(userEntity.getAccount());

        // 將數據寫入響應
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }


    //用於登出
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if(utils.invalidateJwt(authorization)) {
            writer.write(RestBean.success("退出登录成功").asJsonString());

        }else {
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }
    }

    //登入失敗
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized("帳號或密碼錯誤").asJsonString());

    }

    //當用戶已通過身份驗證，但嘗試訪問其角色或權限不允許
    public void onAccessDeny(HttpServletRequest request,
                             HttpServletResponse response,
                             AccessDeniedException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
    }
    //用戶未登入並試圖訪問需要登入的頁面
    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
}
