package com.boottech.springshirojwt.services;

import com.boottech.springshirojwt.common.SecurityConstants;
import com.boottech.springshirojwt.entities.GroupRole;
import com.boottech.springshirojwt.entities.User;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class TokenManagerService {

    private final UserService userService;

    public TokenManagerService(UserService userService) {
        this.userService = userService;
    }

    //Get User Info from the Token
    public User parseUserFromToken(String token){

        Claims claims = Jwts.parser()
            .setSigningKey(SecurityConstants.SECRET)
            .parseClaimsJws(token)
            .getBody();

        Optional<User> currentUser = userService.getById((String) claims.get("id"));
        return currentUser.orElse(null);
    }

    public String createTokenForUser(String token) {
      final String usernameFromToken = getUsernameFromToken(token);
      Optional<User> user = userService.getUserByUsername(usernameFromToken);

      return user.map(value -> Jwts.builder()
              .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
              .setSubject(value.getUsername())
              .claim("id", value.getId())
              .claim("roles", value.getRoles().stream().map(GroupRole::getCode).collect(Collectors.toList()))
              .signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
              .compact()).orElse(null);
    }

    public Boolean validateToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (username.equals(usernameFromToken) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}
