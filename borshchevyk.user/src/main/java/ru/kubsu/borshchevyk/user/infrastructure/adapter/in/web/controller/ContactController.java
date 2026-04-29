package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.user.application.dto.command.LoadContactsCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.RemoveContactCommand;
import ru.kubsu.borshchevyk.user.application.port.in.AddContactUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.LoadContactsUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.RemoveContactUseCase;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.AddContactRequest;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response.ContactResponse;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.mapper.PresentationContactMapper;

import java.util.List;

/**
 * REST controller for managing user contacts.
 *
 * @author Aleksey Timko
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
@Tag(name = "Contact API", description = "Operations related to user contacts")
public class ContactController {

    private final AddContactUseCase addContactUseCase;
    private final LoadContactsUseCase loadContactsUseCase;
    private final RemoveContactUseCase removeContactUseCase;
    private final PresentationContactMapper mapper;

    @Operation(summary = "Get my contacts", description = "Loads the contact list for the current user")
    @ApiResponse(responseCode = "200", description = "Contacts loaded")
    @GetMapping
    public ResponseEntity<List<ContactResponse>> getContacts(
            @RequestHeader("X-User-Id") String ownerId) {
        log.info("Loading contacts for ownerId: [{}]", ownerId);
        var command = LoadContactsCommand.builder().ownerId(ownerId).build();
        var contacts = loadContactsUseCase.loadContacts(command);
        log.info("Successfully loaded {} contacts for ownerId: [{}]", contacts.size(), ownerId);
        return ResponseEntity.ok(contacts.stream()
                .map(mapper::toContactResponse)
                .toList());
    }

    @Operation(summary = "Add a contact", description = "Adds a user to the contact list")
    @ApiResponse(responseCode = "201", description = "Contact added")
    @PostMapping
    public ResponseEntity<ContactResponse> addContact(
            @RequestHeader("X-User-Id") String ownerId,
            @RequestBody @Valid AddContactRequest request) {
        log.info("Adding contact for ownerId: [{}]", ownerId);
        var command = mapper.toAddContactCommand(request, ownerId);
        var contact = addContactUseCase.addContact(command);
        log.info("Successfully added contact for ownerId: [{}]", ownerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toContactResponse(contact));
    }

    @Operation(summary = "Delete a contact", description = "Removes a user from the contact list")
    @ApiResponse(responseCode = "204", description = "Contact deleted")
    @DeleteMapping("/{contactUserId}")
    public ResponseEntity<Void> deleteContact(
            @RequestHeader("X-User-Id") String ownerId,
            @PathVariable String contactUserId) {
        log.info("Deleting contact [{}] for ownerId: [{}]", contactUserId, ownerId);
        var command = RemoveContactCommand.builder()
                .ownerId(ownerId)
                .targetUserId(contactUserId)
                .build();
        removeContactUseCase.removeContact(command);
        log.info("Successfully deleted contact [{}] for ownerId: [{}]", contactUserId, ownerId);
        return ResponseEntity.noContent().build();
    }
}