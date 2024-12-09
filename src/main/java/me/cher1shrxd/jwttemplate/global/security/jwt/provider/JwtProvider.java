package me.cher1shrxd.jwttemplate.global.security.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.cher1shrxd.jwttemplate.domain.auth.repository.RefreshTokenRepository;
import me.cher1shrxd.jwttemplate.domain.user.entity.UserEntity;
import me.cher1shrxd.jwttemplate.domain.user.repository.UserRepository;
import me.cher1shrxd.jwttemplate.global.exception.CustomErrorCode;
import me.cher1shrxd.jwttemplate.global.exception.CustomException;
import me.cher1shrxd.jwttemplate.global.security.details.CustomUserDetails;
import me.cher1shrxd.jwttemplate.global.security.jwt.config.JwtProperties;
import me.cher1shrxd.jwttemplate.global.security.jwt.dto.JwtResponse;
import me.cher1shrxd.jwttemplate.global.security.jwt.enums.JwtType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private SecretKey key;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
    }

    public JwtResponse generateToken(String email) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .header()
                .type(JwtType.ACCESS.name())
                .and()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .header()
                .type(JwtType.REFRESH.name())
                .and()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getRefreshExpiration()))
                .signWith(key)
                .compact();

        refreshTokenRepository.save(email, refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        System.out.println("Parsed JWT Subject (email): " + claims.getSubject());

        if (getType(token) != JwtType.ACCESS) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN_TYPE);
        }

        UserEntity userEntity = userRepository.findByEmail(claims.getSubject())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        UserDetails userDetails = new CustomUserDetails(userEntity);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(CustomErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(CustomErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (MalformedJwtException e) {
            throw new CustomException(CustomErrorCode.MALFORMED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(CustomErrorCode.INVALID_JWT_TOKEN);
        }
    }

    public JwtType getType(String token) {
        return JwtType.valueOf(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getHeader().getType()
        );
    }

}
