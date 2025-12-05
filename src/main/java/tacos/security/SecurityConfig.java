package tacos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;

import java.util.Collections;

import tacos.data.UserRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepo) {
    return username -> {
      tacos.User user = userRepo.findByUsername(username);
      if (user != null) {
        return user;
      }
      throw new UsernameNotFoundException("User '" + username + "' not found");
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            // 配料管理页面 - 需要 OAuth2 登录获取 token
            .requestMatchers("/admin/ingredients/**").authenticated()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/design", "/orders", "/orders/**").hasRole("USER")
            // 资源服务器 API 权限控制
            .requestMatchers(HttpMethod.POST, "/api/ingredients")
                .hasAuthority("SCOPE_writeIngredients")
            .requestMatchers(HttpMethod.DELETE, "/api/ingredients/**")
                .hasAuthority("SCOPE_deleteIngredients")
            // Spring Data REST 端点需要认证
            .requestMatchers(HttpMethod.POST, "/ingredients").hasAuthority("SCOPE_writeIngredients")
            .requestMatchers(HttpMethod.DELETE, "/ingredients/**").hasAuthority("SCOPE_deleteIngredients")
            .requestMatchers("/", "/**").permitAll())
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/design", true))
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .defaultSuccessUrl("/design", true)
            .userInfoEndpoint(userInfo -> userInfo
                .userService(oauth2UserService())
                .oidcUserService(oidcUserService())))
        .logout(logout -> logout
            .logoutSuccessUrl("/"))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**", "/api/**", "/ingredients/**"))
        .headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin()))
        .build();
  }

  // 处理 GitHub 等普通 OAuth2 登录
  private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
    DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    return request -> {
      OAuth2User oauth2User = delegate.loadUser(request);
      // GitHub 登录用户授予 ROLE_USER 权限
      return new DefaultOAuth2User(
          Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
          oauth2User.getAttributes(),
          "login"  // GitHub 用户名属性
      );
    };
  }

  // 处理 taco-admin-client 等 OIDC 登录（带 openid scope）
  private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
    OidcUserService delegate = new OidcUserService();
    return request -> {
      OidcUser oidcUser = delegate.loadUser(request);
      // Taco Auth 登录用户授予 ROLE_USER 权限
      return new DefaultOidcUser(
          Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
          oidcUser.getIdToken(),
          oidcUser.getUserInfo(),
          "sub"
      );
    };
  }

}