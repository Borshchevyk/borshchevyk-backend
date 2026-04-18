package ru.kubsu.borshchevyk.user.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(final MockitoExtension.class)
class SearchUsersServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private PrivacySettingsPort privacySettingsPort;
    @Mock
    private ContactPort contactPort;

    @InjectMocks
    private SearchUsersService searchUsersService;

    private User testUser;
    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        testUser = User.builder(final )
                .userId(userId)
                .email(new Email("test@example.com"))
                .tag(new Tag("test_user"))
                .build();
    }

    @Test
    void searchUsers_shouldReturnUser_whenPublic() {
        SearchUsersCommand command = SearchUsersCommand.builder(final )
                .query("test")
                .build();
        
        PrivacySettings settings = PrivacySettings.builder(final )
                .userId(userId)
                .emailVisibility(Visibility.EVERYONE)
                .build();

        when(loadUserPort.searchUsers("test")).thenReturn(List.of(testUser));
        when(privacySettingsPort.loadByUserId(userId)).thenReturn(Optional.of(settings));

        List<User> result = searchUsersService.searchUsers(command);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail().getValue());
    }
}
