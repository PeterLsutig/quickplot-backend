package eu.nasuta.security.service;

import eu.nasuta.model.User;
import eu.nasuta.model.UserAuthentication;
import eu.nasuta.repository.UserRepository;
import eu.nasuta.security.SecurityConstants;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class JsonWebTokenAuthenticationService implements TokenAuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Value("${security.token.secret.key}")
    private String secretKey;

    @Override
    public Authentication authenticate(final HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.AUTH_HEADER_NAME);
        final Jws<Claims> tokenData = parseToken(token);
        if (tokenData != null) {
            User user = getUserFromToken(tokenData);
            if (user != null) {
                return new UserAuthentication(user);
            }
        }
        return null;
    }

    public Jws<Claims> parseToken(final String token) {
        if (token != null) {
            try {
                return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                    | SignatureException | IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public User getUserFromToken(final Jws<Claims> tokenData) { //ToDo: if present check
        return userRepository.findById(tokenData.getBody().get("username").toString()).get();
    }
}
