package com.ayush.expense_tracker_system.service;


import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@ActiveProfiles("test")
public class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateJwtTokenAndExtractClaims(){
        User user = new User("test@mail.com", "tester", "test@123", Role.ROLE_USER);
        String jwtToken = jwtService.generateJwtToken(user);

        Claims claims = jwtService.extractClaims(jwtToken);
        String username = claims.getSubject(), role = claims.get("role", String.class);

        assertThat(jwtToken).isNotBlank();
        assertThat(claims).isNotNull();
        assertThat(username).isEqualTo("test@mail.com");
        assertThat(role).isEqualTo(user.getRole().name());
    }


    @Test
    void shouldNotExtractClaimsFromMalformedToken(){
        User user = new User("test@mail.com", "tester", "test@123", Role.ROLE_USER);
        String jwtToken = jwtService.generateJwtToken(user);
        String malformedToken = jwtToken+"malformed";

        assertThrows(
                JwtException.class,
                () -> jwtService.extractClaims(malformedToken)
        );
    }
}
