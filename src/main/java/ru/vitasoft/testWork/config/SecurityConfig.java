package ru.vitasoft.testWork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll() //todo настроить по ролям
                .anyRequest()
                .authenticated()
                .and()
                .formLogin();
//                .and() //todo: а нужен ли логаут?
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/", "POST")) //todo: нарисовать страницу
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/login");

        return httpSecurity.build();
    }
}
