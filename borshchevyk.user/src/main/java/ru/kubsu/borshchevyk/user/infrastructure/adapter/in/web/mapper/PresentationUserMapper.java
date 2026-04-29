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
 * Mapper for converting between User-related presentation DTOs and internal application commands or domain models.
 *
 * @author Aleksey Timko
 */
@Mapper(componentModel = "spring")
public interface PresentationUserMapper {

    /**
     * Maps a User domain entity to a UserProfileResponse DTO.
     *
     * @param user the user domain entity
     * @return the user profile response DTO
     */
    @Mapping(target = "userId", expression = "java(user.getUserId() != null && user.getUserId().getValue() != null ? user.getUserId().getValue().toString() : null)")
    @Mapping(target = "email", expression = "java(user.getEmail() != null ? user.getEmail().getValue() : null)")
    @Mapping(target = "tag", expression = "java(user.getTag() != null ? user.getTag().getValue() : null)")
    @Mapping(target = "avatars", source = "avatars")
    UserProfileResponse toUserProfileResponse(User user);

    /**
     * Maps a PrivacySettings domain entity to a PrivacySettingsResponse DTO.
     *
     * @param privacySettings the privacy settings domain entity
     * @return the privacy settings response DTO
     */
    @Mapping(target = "userId", expression = "java(privacySettings.getUserId() != null && privacySettings.getUserId().getValue() != null ? privacySettings.getUserId().getValue().toString() : null)")
    PrivacySettingsResponse toPrivacySettingsResponse(PrivacySettings privacySettings);

    /**
     * Maps an UpdateProfileRequest and userId to an UpdateProfileCommand.
     *
     * @param request the update profile request DTO
     * @param userId the user ID
     * @return the update profile command
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "bio", source = "request.bio")
    @Mapping(target = "avatarUrl", source = "request.avatarUrl")
    UpdateProfileCommand toUpdateProfileCommand(UpdateProfileRequest request, String userId);

    /**
     * Maps an UpdatePrivacySettingsRequest and userId to an UpdatePrivacySettingsCommand.
     *
     * @param request the update privacy settings request DTO
     * @param userId the user ID
     * @return the update privacy settings command
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "emailVisibility", source = "request.emailVisibility")
    @Mapping(target = "searchByEmailVisibility", source = "request.searchByEmailVisibility")
    @Mapping(target = "profilePhotoVisibility", source = "request.profilePhotoVisibility")
    @Mapping(target = "inviteToChatVisibility", source = "request.inviteToChatVisibility")
    UpdatePrivacySettingsCommand toUpdatePrivacySettingsCommand(UpdatePrivacySettingsRequest request, String userId);
}
