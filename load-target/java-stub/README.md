# Kafka Client Processor

Spring Boot приложение, которое:

1. Слушает Kafka-топик `topic1`
2. Извлекает из JSON поля `msg_id`, `inn`, `full_name`
3. Сохраняет данные в PostgreSQL (таблица `clients`)
4. Отправляет в топик `message-out` JSON с `id` записи и временем сохранения

## Подключение

| Сервис     | Адрес                    |
|------------|--------------------------|
| Kafka      | `161.104.32.243:9092`    |
| PostgreSQL | `161.104.32.243:5432`    |
| База       | `load_db`                |
| Таблица    | `clients`                |
| Пользователь | `load`                 |

## Формат входящего сообщения (topic1)

```json
{
  "msg_id": "abc-123",
  "inn": "7707083893",
  "full_name": "Иванов Иван Иванович"
}
```

## Формат исходящего сообщения (message-out)

```json
{
  "id": 1,
  "time": "2026-02-12 15:06"
}
```

## Таблица clients

| Столбец    | Описание                          |
|------------|-----------------------------------|
| id         | Автоинкремент (PK)                |
| msg_id     | Значение `msg_id` из сообщения    |
| full_name  | Значение `full_name` из сообщения |
| inn        | Значение `inn` из сообщения       |
| time       | Время записи в формате `yyyy-MM-dd HH:mm` |

## Запуск

```bash
mvn spring-boot:run
```

Параметры подключения заданы в `application.yml`. При необходимости их можно переопределить переменными окружения:

| Переменная                 | По умолчанию              |
|----------------------------|---------------------------|
| `DB_HOST`                  | `161.104.32.243`          |
| `DB_PORT`                  | `5432`                    |
| `DB_NAME`                  | `load_db`                 |
| `DB_USER`                  | `load`                    |
| `DB_PASSWORD`              | *(см. application.yml)*   |
| `KAFKA_BOOTSTRAP_SERVERS`  | `161.104.32.243:9092`     |
| `KAFKA_INPUT_TOPIC`        | `topic1`                  |
| `KAFKA_OUTPUT_TOPIC`       | `message-out`             |
| `KAFKA_CONSUMER_GROUP`     | `client-processor-group`  |
