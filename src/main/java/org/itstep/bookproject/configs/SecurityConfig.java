package org.itstep.bookproject.configs;

import org.itstep.bookproject.services.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/books/**").hasRole("USER")
                .antMatchers("/users/**").hasRole("USER")
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/authors/**").hasRole("USER")
                .antMatchers("/tags/**").hasRole("USER")
                .antMatchers("/books-requests/**").hasRole("USER")
                .antMatchers("/edit/**").hasRole("MOD")
                .and().csrf().disable();
        httpSecurity.formLogin().loginPage("/login").permitAll()
                .loginProcessingUrl("/auth").permitAll()
                .usernameParameter("email")
                .passwordParameter("password")
                .successForwardUrl("/home")
                .defaultSuccessUrl("/home")
                .failureUrl("/login?error");
        httpSecurity.logout().logoutUrl("/logout")
                .logoutSuccessUrl("/login").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }
}
