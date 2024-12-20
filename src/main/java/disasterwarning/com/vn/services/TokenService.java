package disasterwarning.com.vn.services;

import disasterwarning.com.vn.components.JwtTokenUtils;
import disasterwarning.com.vn.models.dtos.TokenDTO;
import disasterwarning.com.vn.models.entities.Token;
import disasterwarning.com.vn.models.entities.User;
import disasterwarning.com.vn.repositories.TokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    private static final int MAX_TOKENS = 3;
    private final Mapper mapper;
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final TokenRepo tokenRepository;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    public Token addToken(User user, String token) {
        List<Token> userTokens = tokenRepository.findByUser(user.getUserId());
        int tokenCount = userTokens.size();

        // Kiểm tra số lượng token và xóa token nếu vượt quá số lượng tối đa
        if (tokenCount >= MAX_TOKENS) {
            // Xóa token đầu tiên trong danh sách
            Token tokenToDelete = userTokens.get(0);
            tokenRepository.delete(tokenToDelete);
        }

        // Tạo token mới
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds);
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .build();
        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);

        return newToken;
    }

    @Override
    public List<Token> getAllTokensByUser(int id) {
        return tokenRepository.findByUser(id);
    }

    @Override
    public void deleteToken(String token) {
        Token tokenEntity = tokenRepository.findByToken(token);
        if (tokenEntity == null) {
            throw new RuntimeException("Token not found.");
        } else {
            tokenRepository.delete(tokenEntity);
        }
    }

    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if (existingToken == null) {
            throw new Exception("Token not found");
        }
        if(existingToken.getRefreshExpirationDate().compareTo(LocalDateTime.now()) < 0){
            tokenRepository.delete(existingToken);
            throw new Exception("Refresh token is expired");
        }
        String token = jwtTokenUtil.generateToken(user);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
        existingToken.setExpirationDate(expirationDateTime);
        existingToken.setToken(token);
        existingToken.setRefreshToken(UUID.randomUUID().toString());
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        return existingToken;
    }

    @Override
    public Number getCountToken(int id) throws Exception {
        List<Token> tokens = tokenRepository.findByUser(id);
        if (tokens.isEmpty()) {
            throw new Exception("Token not found");
        }
        Integer countToken = 0;
        for (Token token : tokens) {
            countToken = countToken + 1;
        }
        return countToken;
    }
}
