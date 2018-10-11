package eu.nasuta.security.service;

import eu.nasuta.exception.ServiceException;
import eu.nasuta.model.IUser;
import eu.nasuta.repository.IUserRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonWebTokenService implements TokenService {

    @Autowired
    IUserRepository userRepository;

    private static int tokenExpirationTime = 30;

    @Value("${security.token.secret.key}")
    private String tokenKey;

    @Override
    public String getToken(String username, String password) {
        if (username == null || password == null) return null;

        final IUser user = userRepository.findById(username).orElse(null);
        if (user == null) throw new ServiceException("Authentication error", this.getClass().getName());
        Map<String, Object> tokenData = new HashMap<>();
        if (password.equals(user.getPassword())) {
            tokenData.put("username", user.getUsername());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, tokenExpirationTime);
            tokenData.put("token_expiration_date", calendar.getTime());
            JwtBuilder jwtBuilder = Jwts.builder();
            jwtBuilder.setExpiration(calendar.getTime());
            jwtBuilder.setClaims(tokenData);
            return jwtBuilder.signWith(SignatureAlgorithm.HS512, tokenKey).compact();
        }
        else {
            throw new ServiceException("Authentication error", this.getClass().getName());
        }
    }
}
