# Руководство для Mobile-разработчика (Gemini) по интеграции с Borshchevyk Backend

Этот документ содержит сводку всех реализованных функций бэкенда (по состоянию на 9 апреля 2026 г.) и служит архитектурным гидом для создания мобильного приложения.

## 1. Общая архитектура (Куда смотреть)
Бэкенд построен на **Java 21 + Spring Boot 3** с использованием **Гексагональной архитектуры (Ports and Adapters)** и микросервисов.
*   **Где искать эндпоинты (REST API):** Во всех микросервисах смотри папку `src/main/java/ru/kubsu/borshchevyk/<service>/infrastructure/adapter/in/web/controller/`.
*   **Где искать контракты (DTO):** Там же, в папке `.../web/dto/request/` и `.../web/dto/response/`. Все DTO аннотированы Swagger/OpenAPI (`@Schema`).
*   **API Gateway:** Все REST-запросы мобильного приложения должны идти через единую точку входа (API Gateway) на порту `8080` (по умолчанию). Gateway сам проверяет JWT-токен и маршрутизирует запросы.

---

## 2. Микросервис Авторизации (`borshchevyk.auth`)
*Реализует криптографический Challenge-Response флоу авторизации.*
*   **Регистрация (`POST /api/v1/auth/register`):** Теперь требует обязательное поле `firstName` (имя пользователя).
*   **Авторизация:**
    1.  `POST /login` -> Выдает публичный ключ и базовые данные.
    2.  `POST /challenge` -> Генерирует случайную строку (challenge) для подписи на клиенте.
    3.  `POST /verify` -> Принимает подписанный challenge. Возвращает `accessToken` и `refreshToken`.
*   **Обновление токена (`POST /api/v1/auth/refresh`):** Принимает `refreshToken` в теле (`RefreshRequest`), возвращает новую пару токенов (`VerifyResponse`).

---

## 3. Микросервис Пользователей (`borshchevyk.user`)
*Управляет профилями, настройками приватности (как в Telegram), поиском и контактами.*

### 3.1. Профиль и Приватность
*   **Профиль:** Включает `firstName`, `lastName`, `bio`, `avatarUrl`. (Тег `@tag` используется как уникальный username).
*   **Приватность:** 4 настройки (`emailVisibility`, `searchByEmailVisibility`, `profilePhotoVisibility`, `inviteToChatVisibility`) со значениями `EVERYONE`, `CONTACTS`, `NOBODY`.
*   **Эндпоинты:**
    *   `PATCH /api/v1/users/me/profile` — обновить профиль.
    *   `GET /api/v1/users/me/privacy` и `PATCH /api/v1/users/me/privacy` — управление приватностью.
    *   `GET /api/v1/users/{userIdOrTag}` — получить чужой профиль. **Внимание:** Бэкенд автоматически затирает приватные поля (например, отдает `email: null`), если настройки пользователя запрещают их просмотр.

### 3.2. Глобальный поиск и Контакты
*   **Поиск (`GET /api/v1/users/search?query=...`):** Ищет по префиксу тега (`@aleks`) и подстрокам имени/фамилии. Поиск по email сработает только при точном совпадении и если у цели разрешен `searchByEmailVisibility`.
*   **Контакты:** Локальная адресная книга пользователя.
    *   `GET /api/v1/contacts`, `POST /api/v1/contacts`, `DELETE /api/v1/contacts/{userId}`.

---

## 4. Микросервис Сообщений (`borshchevyk.message`)
*Реализует чаты, группы, каналы и реалтайм-доставку (WebSockets).*

### 4.1. Управление чатами и правами
*   **Типы чатов:** `PRIVATE`, `GROUP`, `CHANNEL`.
*   **Роли:** `OWNER`, `ADMIN`, `MEMBER`.
*   **Гранулярные права:** `canSendMessages`, `canDeleteMessages`, `canInviteUsers`, `canChangeInfo`.
*   **Эндпоинты:**
    *   `POST /api/v1/chats` (создать), `GET /api/v1/chats` (список).
    *   `PATCH /api/v1/chats/{chatId}/members/{userId}/permissions` — изменение прав (только для OWNER/ADMIN).

### 4.2. Сообщения и Удаление
*   **Отправка:** `POST /api/v1/chats/{chatId}/messages`
*   **Удаление (`DELETE /api/v1/chats/{chatId}/messages/{messageId}?forAll=true/false`):**
    *   `forAll=true`: Сообщение удаляется у всех (отправляется WebSocket-событие об удалении).
    *   `forAll=false`: Сообщение локально скрывается только для текущего пользователя (заносится в таблицу скрытых).
*   **Очистка истории (`DELETE /api/v1/chats/{chatId}/history?forAll=...`):**
    *   Устанавливает `historyClearedAt`. При запросе `GET /api/v1/chats/{chatId}/messages` бэкенд не будет отдавать старые сообщения.

### 4.3. Real-time (WebSockets + STOMP)
*   **Эндпоинт подключения:** `ws://<domain>:<port>/ws/messages` (Обычно идет в обход Gateway напрямую на Message Service или через специальный роут).
*   **АВТОРИЗАЦИЯ WEBSOCKET:** При отправке STOMP-кадра `CONNECT` мобильное приложение **обязано** передать заголовок `Authorization: Bearer <token>`. Иначе `JwtChannelInterceptor` разорвет соединение.
*   **Подписка:** После успешного коннекта клиент должен подписаться на: `/user/queue/messages` (Spring сам преобразует это в персональную очередь пользователя на основе JWT).
*   **События:** Все входящие сообщения и системные уведомления (например, `MessageDeletedEvent`) будут прилетать в этот топик. Внутренняя синхронизация между серверами реализована через Redis Pub/Sub.