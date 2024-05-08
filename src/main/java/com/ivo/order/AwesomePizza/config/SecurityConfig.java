package com.ivo.order.AwesomePizza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/addPizza/**").permitAll()
                                .requestMatchers("/pizzaOrders/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                );
        http.csrf().disable();
        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user").password("{bcrypt}$2a$10$8i0jTmBZGfzEmLvf2wZYw.FUz8oX0tH4eTT0P0KroHwYI8bHlGjXm").roles("USER").build();
        UserDetails admin = User.withUsername("admin").password("{bcrypt}$2a$10$8i0jTmBZGfzEmLvf2wZYw.FUz8oX0tH4eTT0P0KroHwYI8bHlGjXm").roles("ADMIN").build();
        UserDetails pizzamaker = User.withUsername("pizzamaker").password("{bcrypt}$2a$10$8i0jTmBZGfzEmLvf2wZYw.FUz8oX0tH4eTT0P0KroHwYI8bHlGjXm").roles("PIZZAMAKERS").build();
        return new InMemoryUserDetailsManager(user, admin, pizzamaker);
    }
}
