package com.example.kafkaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа приложения.
 * После запуска Spring Boot поднимает Kafka-consumer топик topic1
 * подключается к PostgreSQL и начинает обрабатывать входящие сообщения.
 */
@SpringBootApplication
public class KafkaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaClientApplication.class, args);
    }
}
