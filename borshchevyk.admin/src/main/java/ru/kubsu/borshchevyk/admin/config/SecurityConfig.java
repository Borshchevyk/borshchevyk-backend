package ru.kubsu.borshchevyk.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.Customizer;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security configuration for Spring Boot Admin.
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final AdminServerProperties adminServer;

    public SecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.getContextPath() + "/");

        http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                .requestMatchers(new AntPathRequestMatcher(this.adminServer.getContextPath() + "/assets/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher(this.adminServer.getContextPath() + "/login")).permitAll()
                .anyRequest().authenticated()
        ).formLogin((formLogin) -> formLogin
                .loginPage(this.adminServer.getContextPath() + "/login")
                .successHandler(successHandler)
        ).logout((logout) -> logout
                .logoutUrl(this.adminServer.getContextPath() + "/logout")
        ).httpBasic(Customizer.withDefaults())
         .csrf((csrf) -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher(this.adminServer.getContextPath() + "/instances", "POST"),
                        new AntPathRequestMatcher(this.adminServer.getContextPath() + "/instances/*", "DELETE"),
                        new AntPathRequestMatcher(this.adminServer.getContextPath() + "/actuator/**")
                )
        )
        .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    private static class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken(); // Force token resolution to write the cookie
            }
            filterChain.doFilter(request, response);
        }
    }
}
