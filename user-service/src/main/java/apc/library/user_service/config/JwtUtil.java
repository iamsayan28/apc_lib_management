// package apc.library.user_service.config;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Decoders; 
// import io.jsonwebtoken.security.Keys; 
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Component;

// import java.nio.charset.StandardCharsets;
// import java.security.Key; 
// import java.util.Date;
// import java.util.HashMap;
// import java.util.function.Function;
// import java.util.stream.Collectors;
// import java.util.Map;
// @Component
// public class JwtUtil {
//     // Note: This key should be much longer and stored securely in your application.properties
//     private final String SECRET_KEY = "your_very_secret_key_that_is_long_enough_for_the_algorithm";

//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimsResolver.apply(claims);
//     }

//     private Claims extractAllClaims(String token) {
//         // Updated to use the new Key object
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSignInKey())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     // public String generateToken(UserDetails userDetails) {
//     //     return Jwts.builder()
//     //             .setSubject(userDetails.getUsername())
//     //             .setIssuedAt(new Date(System.currentTimeMillis()))
//     //             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
//     //             .signWith(getSignInKey(), SignatureAlgorithm.HS256) 
//     //             .compact();
//     // }

//     public String generateToken(UserDetails userDetails) {
//     Map<String, Object> claims = new HashMap<>();
//     // Extract authorities (roles) and add them to the claims
//     claims.put("roles", userDetails.getAuthorities().stream()
//             .map(auth -> auth.getAuthority()).collect(Collectors.toList()));

//     return Jwts.builder()
//             .setClaims(claims) // Set the claims
//             .setSubject(userDetails.getUsername())
//             .setIssuedAt(new Date(System.currentTimeMillis()))
//             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//             .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//             .compact();
//     }

//     // helper method to create the Key
//     private Key getSignInKey() {
//         // byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//         byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        
//         return Keys.hmacShaKeyFor(keyBytes);
//     }

//     public Boolean validateToken(String token, UserDetails userDetails) {
//         final String username = extractUsername(token);
//         return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//     }

//     private Boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }

//     public Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }
// }

package apc.library.user_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;

@Component
public class JwtUtil {
    // Use a proper Base64 encoded secret key (256-bit for HS256)
    private final String SECRET_KEY = "dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RBdXRoZW50aWNhdGlvbjEyMzQ1Njc4OTA=";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Extract authorities (roles) and add them to the claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority()).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims) // Set the claims
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // helper method to create the Key
    private Key getSignInKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}