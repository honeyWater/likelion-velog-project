package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.RefreshToken;
import org.example.velogproject.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // refreshToken 저장 및 업데이트
    @Transactional
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    // user로 존재하는 refreshToken 확인
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    // 로그아웃 시 refreshToken 삭제
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.findByValue(refreshToken).ifPresent(refreshTokenRepository::delete);
    }
}
