# Refactoring Checklist

## Application
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/Application.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/package-info.java`

## Application Layer (DTOs)
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/AddContactCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/EditUserCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/GetUserProfileCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/GetUsersBatchCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/LoadContactsCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/package-info.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/RemoveContactCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/SearchUsersCommand.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/UpdatePrivacySettingsCommand.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/dto/command/UpdateProfileCommand.java`

## Application Layer (Ports)
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/AddContactUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/CreateUserUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/DeleteUserUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/EditUserUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/GetPrivacySettingsUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/GetUserProfileUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/GetUsersBatchUseCase.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/LoadContactsUseCase.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/package-info.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/RemoveContactUseCase.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/SearchUsersUseCase.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/UpdatePrivacySettingsUseCase.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/in/UpdateProfileUseCase.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/ContactPort.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/DeleteUserPort.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/LoadUserPort.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/PrivacySettingsPort.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/SaveUserPort.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/port/out/UserEventPublisherPort.java`

## Application Layer (Services)
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/AddContactService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/CreateUserService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/DeleteUserService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/EditUserService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/GetUserProfileService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/GetUsersBatchService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/LoadContactsService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/PrivacyService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/RemoveContactService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/SearchUsersService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/application/service/UpdateProfileService.java`

## Configuration
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/config/HttpExchangeConfig.java`
- [ ] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/config/package-info.java`

## Domain Layer
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/event/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/event/UserDeletedEvent.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/event/UserRegisteredEvent.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/event/UserUpdatedEvent.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/IncorrectInputFormatException.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/UserAlreadyExistsException.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/UserErrorResponse.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/UserForbiddenException.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/UserNotFoundException.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/exception/UserServiceException.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/contact/Contact.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/contact/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/privacy/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/privacy/PrivacySettings.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/privacy/Visibility.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/result/EditUserResult.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/result/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/user/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/user/User.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/value/Email.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/value/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/value/Tag.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/domain/model/value/UserId.java`

## Infrastructure Layer
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/grpc/UserGrpcService.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/messaging/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/controller/ContactController.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/controller/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/controller/UserController.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/request/AddContactRequest.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/request/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/request/SetAvatarRequest.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/request/UpdatePrivacySettingsRequest.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/request/UpdateProfileRequest.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/response/ContactResponse.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/response/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/response/PrivacySettingsResponse.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/dto/response/UserProfileResponse.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/mapper/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/mapper/PresentationContactMapper.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/in/web/mapper/PresentationUserMapper.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/out/messaging/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/out/persistence/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/out/persistence/entity/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/out/persistence/mapper/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/adapter/out/persistence/repository/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/exception/GlobalExceptionHandler.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/exception/package-info.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/messaging/KafkaUserEventPublisherAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/messaging/KafkaUserRegisteredEventConsumerAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/ContactAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/DeleteUserAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/LoadUserAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/PrivacySettingsAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/SaveUserAdapter.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/entity/ContactEntity.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/entity/PrivacySettingsEntity.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/entity/UserJpaEntity.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/mapper/UserPersistenceMapper.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/repository/ContactRepository.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/repository/PrivacySettingsRepository.java`
- [x] `borshchevyk.user/src/main/java/ru/kubsu/borshchevyk/user/infrastructure/persistence/repository/UserSpringRepository.java`
