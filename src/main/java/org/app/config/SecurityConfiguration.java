package org.app.config;

import org.app.enums.Role;
import org.app.service.impl.impl.MyBCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyBCryptPasswordEncoder myBCryptPasswordEncoder;

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring()
                .antMatchers("/css/**")
                .antMatchers("/js/**")
                .antMatchers("/images/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authentication) throws Exception {
        String userQuery = "SELECT username, password, active FROM users WHERE username = ?";
        String roleQuery =
                "SELECT u.username, ur.role "
                + "FROM users u "
                + "INNER JOIN user_roles ur ON ur.user_id = u.id "
                + "WHERE u.username = ?"
                + "AND u.active = true";

        authentication.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(userQuery)
                .authoritiesByUsernameQuery(roleQuery)
                .passwordEncoder(myBCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/denied").permitAll()
                .antMatchers("/admin/**").hasAnyAuthority(Role.ROLE_ADMIN.name())
                .anyRequest().permitAll();

        httpSecurity.csrf().disable();

        httpSecurity.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/view")
                .failureUrl("/denied")
                .usernameParameter("username")
                .passwordParameter("password");

        httpSecurity.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login");

        httpSecurity.exceptionHandling()
                .accessDeniedPage("/error");
    }
}
