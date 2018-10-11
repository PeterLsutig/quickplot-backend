package eu.nasuta.security.service;

import eu.nasuta.model.IUser;
import eu.nasuta.model.IUserAuthentication;
import eu.nasuta.repository.IUserRepository;
import eu.nasuta.security.SecurityConstants;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class JsonWebTokenAuthenticationService implements TokenAuthenticationService {

    @Autowired
    IUserRepository userRepository;

    @Value("${security.token.secret.key}")
    private String secretKey;

    @Override
    public Authentication authenticate(final HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.AUTH_HEADER_NAME);
        final Jws<Claims> tokenData = parseToken(token);
        if (tokenData != null) {
            IUser user = getUserFromToken(tokenData);
            if (user != null) {
                return new IUserAuthentication(user);
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

    public IUser getUserFromToken(final Jws<Claims> tokenData) { //ToDo: if present check
        return userRepository.findById(tokenData.getBody().get("username").toString()).get();
    }
}
