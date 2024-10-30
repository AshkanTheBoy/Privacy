package org.AshInc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration  // Indicates that this class contains Spring configuration
@EnableWebSecurity  // Enables Spring Security's web security support
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;  // Injects user details service for authentication

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // Creates a BCryptPasswordEncoder bean for password encoding
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Configures the authentication provider to use the custom user details service and password encoder
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // Sets the user details service
        authProvider.setPasswordEncoder(passwordEncoder());  // Sets the password encoder
        return authProvider;  // Returns the configured authentication provider
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Configures security settings for HTTP requests
        return httpSecurity
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(toH2Console()))  // Disables CSRF protection for H2 console
                .authorizeRequests()
                .antMatchers("/signup", "/add/**", "/get/**", "/h2-console/**", "/css/**", "/js/**", "/login").permitAll()  // Allows unrestricted access to these endpoints
                .antMatchers("/main/**", "/chat/**", "/room/**", "/api/**").hasAuthority("USER")  // Requires USER authority for these endpoints
                .anyRequest().authenticated()  // Any other request must be authenticated
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()  // Allows framing of content only from the same origin
                .and()
                .formLogin()
                .loginPage("/customLogin")  // Specifies a custom login page
                .permitAll()  // Allows all users to access the login page
                .and()
                .logout()
                .logoutSuccessUrl("/customLogin")  // Redirects to custom login page after logout
                .permitAll()  // Allows all users to access logout
                .and()
                .build();  // Builds the security filter chain
    }
}
