package com.RESTAPISPRINGBOOTREACT.BACKEND.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;  // Your custom JWT filter
    
    // Configure HTTP security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for JWT authentication
                   .cors(Customizer.withDefaults())  // Enable CORS
                   .authorizeRequests(request -> request
                       .requestMatchers("/auth/**").permitAll()  // Public endpoints for login/register
                       .requestMatchers("/headMaster/**").hasAuthority("HEADMASTER")  // Only HEADMASTER role can access
                       .requestMatchers("/teacher/**").hasAuthority("TEACHER")  // Only TEACHER role can access
                       .requestMatchers("/headMasterteacher/**").hasAnyAuthority("ADMIN", "TEACHER")  // ADMIN/TEACHER access
                       .anyRequest().authenticated()  // All other requests require authentication
                   )
                   .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless authentication
                   .authenticationProvider(authenticationProvider())  // Custom authentication provider
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // JWT filter before username/password authentication filter

        return httpSecurity.build();  // Build and return the security filter chain
    }

    // Define authentication provider with user details service and password encoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);  // Set the custom user details service
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());  // Set BCrypt password encoder
        return daoAuthenticationProvider;
    }

    // Password encoder bean using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();  // Use BCrypt for password encoding
    }

    // Authentication manager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();  // Return authentication manager from configuration
    }
}
