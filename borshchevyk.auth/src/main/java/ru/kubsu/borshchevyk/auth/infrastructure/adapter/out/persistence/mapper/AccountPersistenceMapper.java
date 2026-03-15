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

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountPersistenceMapper {

    @Mapping(target = "id", source = "accountId.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "tag", source = "tag.value")
    AccountJpaEntity toEntity(Account account);

    @Mapping(target = "accountId", source = "id", qualifiedByName = "mapToAccountId")
    @Mapping(target = "email", source = "email", qualifiedByName = "mapToEmail")
    @Mapping(target = "tag", source = "tag", qualifiedByName = "mapToTag")
    Account toDomain(AccountJpaEntity entity);

    @Named("mapToAccountId")
    default AccountId mapToAccountId(UUID id) {
        return id == null ? null : new AccountId(id);
    }

    @Named("mapToEmail")
    default Email mapToEmail(String value) {
        return value == null ? null : new Email(value);
    }

    @Named("mapToTag")
    default Tag mapToTag(String value) {
        return value == null ? null : new Tag(value);
    }
}