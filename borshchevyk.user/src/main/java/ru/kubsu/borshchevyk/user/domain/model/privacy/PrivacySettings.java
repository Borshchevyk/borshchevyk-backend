package ru.kubsu.borshchevyk.user.domain.model.privacy;

import lombok.Builder;
import lombok.Data;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

@Data
@Builder
public class PrivacySettings {
    private UserId userId;
    
    @Builder.Default
    private Visibility emailVisibility = Visibility.NOBODY;
    
    @Builder.Default
    private Visibility searchByEmailVisibility = Visibility.EVERYONE;
    
    @Builder.Default
    private Visibility profilePhotoVisibility = Visibility.EVERYONE;
    
    @Builder.Default
    private Visibility inviteToChatVisibility = Visibility.EVERYONE;
}
