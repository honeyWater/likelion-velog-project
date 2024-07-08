package org.example.velogproject.repository;

import org.example.velogproject.domain.RefreshToken;
import org.example.velogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String refreshToken);
    Optional<RefreshToken> findByUserId(Long userId);
    boolean existsByValue(String refreshToken);
}
