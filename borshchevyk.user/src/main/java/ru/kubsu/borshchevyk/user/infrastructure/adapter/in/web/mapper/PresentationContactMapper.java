package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.user.application.dto.command.AddContactCommand;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.AddContactRequest;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response.ContactResponse;

@Mapper(componentModel = "spring")
public interface PresentationContactMapper {

    @Mapping(target = "id", expression = "java(contact.getId() != null ? contact.getId().toString() : null)")
    @Mapping(target = "ownerId", expression = "java(contact.getOwnerId() != null && contact.getOwnerId().getValue() != null ? contact.getOwnerId().getValue().toString() : null)")
    @Mapping(target = "contactUserId", expression = "java(contact.getContactUserId() != null && contact.getContactUserId().getValue() != null ? contact.getContactUserId().getValue().toString() : null)")
    ContactResponse toContactResponse(Contact contact);

    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "targetUserId", source = "request.targetUserId")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    AddContactCommand toAddContactCommand(AddContactRequest request, String ownerId);
}
