package com.stravamate.repository;

import com.stravamate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStravaAthleteId(Long stravaAthleteId);
}

