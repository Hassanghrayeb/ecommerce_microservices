package com.hamster.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{

    private final AuthEntryPoint authEntryPoint;
    private final AuthorizationFilter authorizationFilter;

    public SecurityConfig(AuthEntryPoint authEntryPoint, AuthorizationFilter authorizationFilter)
    {
        this.authEntryPoint = authEntryPoint;
        this.authorizationFilter = authorizationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception
    {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                                // Actuator
                                .requestMatchers(
                                        mvcMatcherBuilder.pattern("/actuator/metrics/**"),
                                        mvcMatcherBuilder.pattern("/actuator/health/**"),
                                        mvcMatcherBuilder.pattern("/actuator/info/**"),
                                        mvcMatcherBuilder.pattern("/actuator/prometheus/**"),
                                        mvcMatcherBuilder.pattern("/actuator/version/**"),
                                        mvcMatcherBuilder.pattern(HttpMethod.OPTIONS, "/*") // Allow Option calls for FrontEnd
                                                )
                                .permitAll()

                                // Swagger
                                .requestMatchers(
                                        mvcMatcherBuilder.pattern("/api-docs/**"),
                                        mvcMatcherBuilder.pattern("/configuration/ui"),
                                        mvcMatcherBuilder.pattern("/swagger-resources/**"),
                                        mvcMatcherBuilder.pattern("/configuration/security"),
                                        mvcMatcherBuilder.pattern("/swagger-ui/**"),
                                        mvcMatcherBuilder.pattern("/webjars/**")
                                                )
                                .permitAll()

                                // Login, logout, register,
                                .requestMatchers(
                                        mvcMatcherBuilder.pattern("/auth/**"),
                                        mvcMatcherBuilder.pattern("/logout/**"),
                                        mvcMatcherBuilder.pattern("/registration/**")
                                                )
                                .permitAll()

                                .requestMatchers(
                                        mvcMatcherBuilder.pattern("/error")
                                                ).permitAll()

                                // Admin functions
                                .requestMatchers(
                                        mvcMatcherBuilder.pattern("/admin/**")
                                                ).hasAnyAuthority("ADMIN")

                                .anyRequest().authenticated()
                                      )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .rememberMe(Customizer.withDefaults());

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
