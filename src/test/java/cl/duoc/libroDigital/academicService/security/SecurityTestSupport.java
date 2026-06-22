package cl.duoc.libroDigital.academicService.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public final class SecurityTestSupport {

    private SecurityTestSupport() {
    }

    public static void authenticateAs(String username, Long userId, String email, String... roles) {
        JwtUserPrincipal principal = new JwtUserPrincipal(username, userId, email, List.of(roles));
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
