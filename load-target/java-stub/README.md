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

Сборка JAR:

```bash
mvn clean package -DskipTests
java -Xmx750m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof \
  -jar target/kafka-client-processor-1.0.0-SNAPSHOT.jar
```

## Мониторинг (Actuator + Prometheus + Grafana)

### Endpoints Actuator

После запуска доступны на порту **8081**:

| URL | Назначение |
|-----|------------|
| `http://localhost:8081/actuator/health` | Состояние приложения |
| `http://localhost:8081/actuator/prometheus` | Метрики в формате Prometheus |

Собираются стандартные метрики JVM (heap, GC, threads), процесса, HikariCP, JDBC, Kafka и Spring Boot.

### Prometheus

Пример job для `prometheus.yml` — см. [monitoring/prometheus.yml](monitoring/prometheus.yml):

```yaml
scrape_configs:
  - job_name: kafka-client-processor
    metrics_path: /actuator/prometheus
    scrape_interval: 15s
    static_configs:
      - targets:
          - <host-приложения>:8081
```

Перезагрузите конфигурацию Prometheus:

```bash
curl -X POST http://<prometheus-host>:9090/-/reload
```

Проверка в UI Prometheus: **Status → Targets** — job должен быть в состоянии **UP**.

### Grafana

1. **Connections → Data sources → Add data source → Prometheus**
2. URL: `http://<prometheus-host>:9090`
3. **Save & test**

Примеры PromQL-запросов для дашборда:

```promql
# Использование heap JVM
jvm_memory_used_bytes{application="kafka-client-processor", area="heap"}

# Лимит heap
jvm_memory_max_bytes{application="kafka-client-processor", area="heap"}

# GC-пause
rate(jvm_gc_pause_seconds_sum{application="kafka-client-processor"}[5m])

# Активные потоки
jvm_threads_live_threads{application="kafka-client-processor"}

# Пул соединений БД (HikariCP)
hikaricp_connections_active{application="kafka-client-processor"}

# Lag consumer Kafka (если доступна метрика)
kafka_consumer_fetch_manager_records_lag{application="kafka-client-processor"}
```

Готовые дашборды: в Grafana импортируйте **Dashboard ID 4701** (JVM Micrometer) или **11378** (Spring Boot 2.x/3.x Statistics) — укажите ваш Prometheus data source.

Параметры подключения к Kafka и PostgreSQL заданы в `application.yml`.
