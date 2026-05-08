package auth_api.service;

import auth_api.model.RefreshToken;
import auth_api.model.User;
import auth_api.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(
                        Instant.now().plusSeconds(7 * 24 * 60 * 60)
                )
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                ));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        return refreshToken;
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}