package ru.kubsu.borshchevyk.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.application.port.in.DeleteUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.EditUserUseCase;
import ru.kubsu.borshchevyk.user.domain.exception.*;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;
import ru.kubsu.borshchevyk.user.presentation.dto.request.EditUserRequest;
import ru.kubsu.borshchevyk.user.presentation.dto.response.EditUserResponse;
import ru.kubsu.borshchevyk.user.presentation.mapper.UserMapper;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user profiles and data")
public class UserController {

    private final EditUserUseCase editUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserMapper userMapper;

    @Operation(summary = "Edit user profile", description = "Partially updates user profile data such as email or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile successfully updated",
                    content = @Content(schema = @Schema(implementation = EditUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input format",
                    content = @Content(schema = @Schema(implementation = IncorrectInputFormatException.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden to edit other users",
                    content = @Content(schema = @Schema(implementation = UserForbiddenException.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = UserServiceException.class)))
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<EditUserResponse> editUser(
            @Parameter(description = "UUID of the user to edit", required = true)
            @PathVariable String userId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String requestUserId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to update", required = true)
            @RequestBody EditUserRequest request) {
        
        if (!userId.equals(requestUserId)) {
            throw new ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException("You can only edit your own profile");
        }
        
        EditUserCommand command = userMapper.toCommand(userId, request);
        EditUserResult result = editUserUseCase.editUser(command);
        EditUserResponse response = userMapper.toResponse(result);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user", description = "Permanently deletes a user from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden to delete other users",
                    content = @Content(schema = @Schema(implementation = UserErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = UserServiceException.class)))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID of the user to delete", required = true)
            @PathVariable String userId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String requestUserId) {
        
        if (!userId.equals(requestUserId)) {
            throw new ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException("You can only delete your own profile");
        }
        
        deleteUserUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}