package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.ChallengeStorePort;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.time.Duration;
import java.util.Optional;

/**
 * Adapter for storing cryptographic challenges in Redis.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisChallengeStoreAdapter implements ChallengeStorePort {

    private final StringRedisTemplate redisTemplate;
    private static final String CHALLENGE_PREFIX = "challenge:";
    private static final Duration CHALLENGE_TTL = Duration.ofMinutes(5);

    /**
     * Saves a challenge for a given account.
     *
     * @param accountId the account ID
     * @param challenge the challenge string
     */
    @Override
    public void saveChallenge(AccountId accountId, String challenge) {
        log.info("Saving challenge for account: {}", accountId.value());
        String key = CHALLENGE_PREFIX + accountId.value().toString();
        redisTemplate.opsForValue().set(key, challenge, CHALLENGE_TTL);
    }

    /**
     * Retrieves a challenge for a given account.
     *
     * @param accountId the account ID
     * @return the challenge string, if found
     */
    @Override
    public Optional<String> getChallenge(AccountId accountId) {
        log.info("Retrieving challenge for account: {}", accountId.value());
        String key = CHALLENGE_PREFIX + accountId.value().toString();
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    /**
     * Deletes a challenge for a given account.
     *
     * @param accountId the account ID
     */
    @Override
    public void deleteChallenge(AccountId accountId) {
        log.info("Deleting challenge for account: {}", accountId.value());
        String key = CHALLENGE_PREFIX + accountId.value().toString();
        redisTemplate.delete(key);
    }
}
