package com.RESTAPISPRINGBOOTREACT.BACKEND.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)  // Disables CSRF for JWT authentication
                   .cors(Customizer.withDefaults())  // Enable CORS (Cross-Origin Resource Sharing)
                   .authorizeHttpRequests(request -> request
                       .requestMatchers("/auth/**").permitAll()  // Public endpoints (e.g., login, register)
                       .requestMatchers("/headMaster/**").hasAuthority("HEADMASTER")  // Only HEADMASTER role can access /headMaster/** endpoints
                       .requestMatchers("/teacher/**").hasAuthority("TEACHER")  // Only TEACHER role can access /teacher/** endpoints
                       .requestMatchers("/headMasterteacher/**").hasAnyAuthority("ADMIN", "TEACHER")  
                       .anyRequest().authenticated()
                   )
                   .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Makes the application stateless
                   .authenticationProvider(authenticationProvider())  // Use custom AuthenticationProvider
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before UsernamePasswordAuthenticationFilter

        return httpSecurity.build();  // This returns the SecurityFilterChain object
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
       DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
       daoAuthenticationProvider.setUserDetailsService(userDetailsService);  // Set custom UserDetailsService
       daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());  // Set BCryptPasswordEncoder
       return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();  // Use BCrypt for password encoding
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();  
    }
}
