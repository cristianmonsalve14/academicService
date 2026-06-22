package cl.duoc.libroDigital.academicService.security;

import java.util.List;

public class JwtUserPrincipal {

    private final String username;
    private final Long userId;
    private final String email;
    private final List<String> roles;

    public JwtUserPrincipal(String username, Long userId, String email, List<String> roles) {
        this.username = username;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
