package org.recognition.config;

import org.recognition.entity.RoleEntity;
import org.recognition.entity.UserEntity;
import org.recognition.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthProviderAdapter implements AuthenticationProvider {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserService service;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println(username);
        System.out.println(password);
        UserEntity u = service.loadUserByUsername(username);
        if (!encoder.matches(password, u.getPassword())) {
            System.out.println("Invalid credentials");
            throw new BadCredentialsException("Invalid credentials.");
        }
//


        return new UsernamePasswordAuthenticationToken(username, password, u.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}