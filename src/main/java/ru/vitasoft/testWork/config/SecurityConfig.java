package ru.vitasoft.testWork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //оставлю здесь закомментированный код, чтоб показать второй способ решения с доступами
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
//                .antMatchers( "/admin/**").hasAuthority(Permission.ADMIN_PERMISSION.getPermission())
//                .antMatchers( "/operator/**").hasAuthority(Permission.OPERATOR_PERMISSION.getPermission())
//                .antMatchers("/requests/**").hasAuthority(Permission.USER_PERMISSION.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin();

        return httpSecurity.build();
    }


}
