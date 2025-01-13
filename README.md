# Задание
News Control System
## Содержание
- [Стэк](#стэк)
- [Инструкция по развертыванию](#инструкция-по-развертыванию)
- [Примечания](#примечания)


## Стэк
- Java 21
- Gradle 8.5
- PostgreSQL
- Spring Boot
- Spring Data
- Spring Cloud Feign Client
- Lombok
- Mapstruct
- Liquibase
- Redis
- JUnit, Mockito

## Инструкция по развертыванию

1. Скачайте или склонируйте репозиторий с logger стартером по ссылке: https://github.com/pashpashovich/logger_starter
2. Выполните в консоли команду:
```
./gradlew clean build
```
Стартер будет сохранен в локальном Maven репозитории.
3. Скачайте или склонируйте репозиторий с exception стартером по ссылке: https://github.com/pashpashovich/exception_starter
4. Выполните в консоли команду:
```
./gradlew clean build
```
Стартер будет сохранен в локальном Maven репозитории.
5. Скачайте или склонируйте текущий репозиторий и соберите проект.
6. Выполните команду:
```
docker-compose up --build   
```
7. Скачайте или склонируйте репозиторий с микросервисом комментариев: https://github.com/pashpashovich/comments_service
8. Соберите проект и выполните команду:
```
docker-compose up --build   
```
9. Скачайте или склонируйте репозиторий с микросервисом пользователей: https://github.com/pashpashovich/user_service
10. Соберите проект и выполните команду:
```
docker-compose up --build
```
11. Для тестирования API используйте следующие ссылки со Swagger UI:
http://localhost:8081/swagger-ui/index.html
http://localhost:8082/swagger-ui/index.html
http://localhost:8083/swagger-ui/index.html

## Примечания
1. Для работы приложения должны быть свободны порты localhost 8081-8083, 5432-5434,6379.
2. Для получения JWT токена необходимо пройти авторизацию в микросервисе пользователей, далее скопировать токен и использовать его для обращения к двум другим микросервисам. По умолчанию пользователей в БД нет, необходимо пройти регистрацию в микросервисе пользователей, используя одну из ролей (ADMIN, JOURNALIST, SUBSCRIBER)



