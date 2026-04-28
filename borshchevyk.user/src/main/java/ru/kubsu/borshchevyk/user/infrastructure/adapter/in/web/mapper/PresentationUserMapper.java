package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdatePrivacySettingsCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.UpdateProfileRequest;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.UpdatePrivacySettingsRequest;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response.UserProfileResponse;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response.PrivacySettingsResponse;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Mapper(componentModel = "spring")
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface PresentationUserMapper {

    @Mapping(target = "userId", expression = "java(user.getUserId() != null && user.getUserId().getValue() != null ? user.getUserId().getValue().toString() : null)")
    @Mapping(target = "email", expression = "java(user.getEmail() != null ? user.getEmail().getValue() : null)")
    @Mapping(target = "tag", expression = "java(user.getTag() != null ? user.getTag().getValue() : null)")
    @Mapping(target = "avatars", source = "avatars")
    UserProfileResponse toUserProfileResponse(User user);

    @Mapping(target = "userId", expression = "java(privacySettings.getUserId() != null && privacySettings.getUserId().getValue() != null ? privacySettings.getUserId().getValue().toString() : null)")
    PrivacySettingsResponse toPrivacySettingsResponse(PrivacySettings privacySettings);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "bio", source = "request.bio")
    @Mapping(target = "avatarUrl", source = "request.avatarUrl")
    UpdateProfileCommand toUpdateProfileCommand(UpdateProfileRequest request, String userId);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "emailVisibility", source = "request.emailVisibility")
    @Mapping(target = "searchByEmailVisibility", source = "request.searchByEmailVisibility")
    @Mapping(target = "profilePhotoVisibility", source = "request.profilePhotoVisibility")
    @Mapping(target = "inviteToChatVisibility", source = "request.inviteToChatVisibility")
    UpdatePrivacySettingsCommand toUpdatePrivacySettingsCommand(UpdatePrivacySettingsRequest request, String userId);
}

