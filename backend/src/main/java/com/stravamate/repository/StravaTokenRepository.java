package com.stravamate.repository;

import com.stravamate.entity.StravaToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StravaTokenRepository extends JpaRepository<StravaToken, Long> {
    Optional<StravaToken> findByUserId(Long userId);
}

