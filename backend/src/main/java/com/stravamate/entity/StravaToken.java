package com.stravamate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "strava_tokens", indexes = {
        @Index(name = "idx_token_user_id", columnList = "user_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StravaToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 2048)
    private String accessToken;

    @Column(length = 2048)
    private String refreshToken;

    // epoch seconds when access token expires
    private Long expiresAt;
}

