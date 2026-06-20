package com.example.kafkaclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Имена Kafka-топиков из application.yml (секция app.kafka). */
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {

    /** Входной топик, откуда читаются сообщения с данными клиента. */
    private String inputTopic;

    /** Выходной топик, куда отправляется результат сохранения в БД. */
    private String outputTopic;

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }
}
