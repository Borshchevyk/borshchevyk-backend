package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.ChallengeStorePort;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisChallengeStoreAdapter implements ChallengeStorePort {

    private final StringRedisTemplate redisTemplate;
    private static final String CHALLENGE_PREFIX = "challenge:";
    private static final Duration CHALLENGE_TTL = Duration.ofMinutes(5);

    @Override
    public void saveChallenge(AccountId accountId, String challenge) {
        String key = CHALLENGE_PREFIX + accountId.value().toString();
        redisTemplate.opsForValue().set(key, challenge, CHALLENGE_TTL);
    }

    @Override
    public Optional<String> getChallenge(AccountId accountId) {
        String key = CHALLENGE_PREFIX + accountId.value().toString();
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void deleteChallenge(AccountId accountId) {
        String key = CHALLENGE_PREFIX + accountId.value().toString();
        redisTemplate.delete(key);
    }
}
