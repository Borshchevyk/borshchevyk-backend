package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.PrivacySettingsEntity;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.PrivacySettingsRepository;

import java.util.Optional;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Component
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@RequiredArgsConstructor
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public class PrivacySettingsAdapter implements PrivacySettingsPort {

    private final PrivacySettingsRepository repository;

    @Override
    public PrivacySettings save(PrivacySettings privacySettings) {
        PrivacySettingsEntity entity = PrivacySettingsEntity.builder()
                .userId(privacySettings.getUserId().getValue())
                .emailVisibility(privacySettings.getEmailVisibility())
                .searchByEmailVisibility(privacySettings.getSearchByEmailVisibility())
                .profilePhotoVisibility(privacySettings.getProfilePhotoVisibility())
                .inviteToChatVisibility(privacySettings.getInviteToChatVisibility())
                .build();

        PrivacySettingsEntity saved = repository.save(entity);

        return PrivacySettings.builder()
                .userId(new UserId(saved.getUserId()))
                .emailVisibility(saved.getEmailVisibility())
                .searchByEmailVisibility(saved.getSearchByEmailVisibility())
                .profilePhotoVisibility(saved.getProfilePhotoVisibility())
                .inviteToChatVisibility(saved.getInviteToChatVisibility())
                .build();
    }

    @Override
    public Optional<PrivacySettings> loadByUserId(UserId userId) {
        return repository.findById(userId.getValue())
                .map(entity -> PrivacySettings.builder()
                        .userId(new UserId(entity.getUserId()))
                        .emailVisibility(entity.getEmailVisibility())
                        .searchByEmailVisibility(entity.getSearchByEmailVisibility())
                        .profilePhotoVisibility(entity.getProfilePhotoVisibility())
                        .inviteToChatVisibility(entity.getInviteToChatVisibility())
                        .build());
    }
}

