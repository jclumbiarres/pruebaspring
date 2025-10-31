package org.lumbi.ejercicio.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final String token;

    /**
     * Constructor para JwtAuthenticationToken
     * 
     * @param username
     * @param token
     */
    public JwtAuthenticationToken(String username, String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.username = username;
        this.token = token;
        setAuthenticated(true);
    }

    /**
     * Constructor para JwtAuthenticationToken con authorities
     * 
     * @param username
     * @param token
     * @param authorities
     */
    public JwtAuthenticationToken(String username, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.token = token;
        super.setAuthenticated(true);
    }

    /**
     * Obtiene las credenciales (token JWT)
     * 
     * @return Object las credenciales (token JWT)
     */
    @Override
    public Object getCredentials() {
        return token;
    }

    /**
     * Obtiene el principal (nombre de usuario)
     * 
     * @return Object el principal (nombre de usuario)
     */
    @Override
    public Object getPrincipal() {
        return username;
    }
}