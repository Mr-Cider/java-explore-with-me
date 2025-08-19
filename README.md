https://github.com/Mr-Cider/java-explore-with-me/pull/3


# Комментарии к событиям
## Основное
### Private:
- **POST** /users/{userId}/events/{eventId}/comments - *Создание комментария*

- **PATCH** /users/{userId}/events/{eventId}/comments/{commentId} - *Редактирование комментария*

- **DELETE** /users/{userId}/events/{eventId}/comments/{commentId} - *Удаление комментария*

- **GET** /users/{userId}/comments - *Получение всех комментариев текущего пользователя (фильтрация по дате создания и пагинация)*

### Public
- **GET** /events/{eventId}/comments - *Получение всех комментариев к событию (с возможностью фильтрации по дате создания и пагинация)*

- **GET** /comments/{commentId} - *Получение комментария по id*

## Дополнительное
### Admin
- **PATCH** /admin/comments/{commentId} - *Модерация комментария*

- **DELETE** /admin/comments/{commentId} - *Удаление комментария администратором*

- **GET** /admin/comments - *Получение всех комментариев к событию (с возможностью фильтрации по дате)*