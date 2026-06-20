package com.example.kafkaclient.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Общая конфигурация приложения: подключает классы с настройками из application.yml. */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class AppConfig {
}
