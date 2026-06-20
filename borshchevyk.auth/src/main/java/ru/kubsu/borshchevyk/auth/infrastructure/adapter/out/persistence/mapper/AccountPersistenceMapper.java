package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.entity.AccountJpaEntity;

import java.util.UUID;

/**
 * MapStruct mapper for converting between {@link Account} domain model and {@link AccountJpaEntity}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountPersistenceMapper {

    /**
     * Maps domain model to JPA entity.
     *
     * @param account the domain model
     * @return the JPA entity
     */
    @Mapping(target = "id", source = "accountId.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "tag", source = "tag.value")
    AccountJpaEntity toEntity(Account account);

    /**
     * Maps JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model
     */
    @Mapping(target = "accountId", source = "id", qualifiedByName = "mapToAccountId")
    @Mapping(target = "email", source = "email", qualifiedByName = "mapToEmail")
    @Mapping(target = "tag", source = "tag", qualifiedByName = "mapToTag")
    Account toDomain(AccountJpaEntity entity);

    /**
     * Helper for mapping UUID to AccountId.
     *
     * @param id the UUID
     * @return the AccountId
     */
    @Named("mapToAccountId")
    default AccountId mapToAccountId(UUID id) {
        return id == null ? null : new AccountId(id);
    }

    /**
     * Helper for mapping String to Email.
     *
     * @param value the String
     * @return the Email
     */
    @Named("mapToEmail")
    default Email mapToEmail(String value) {
        return value == null ? null : new Email(value);
    }

    /**
     * Helper for mapping String to Tag.
     *
     * @param value the String
     * @return the Tag
     */
    @Named("mapToTag")
    default Tag mapToTag(String value) {
        return value == null ? null : new Tag(value);
    }
}
