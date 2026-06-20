package com.example.kafkaclient.kafka;

import com.example.kafkaclient.dto.InboundMessage;
import com.example.kafkaclient.model.Client;
import com.example.kafkaclient.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель входящих Kafka-сообщений.
 * Поток обработки:
 * 1. Получить JSON из value топика topic1 (key не используется)
 * 2. Распарсить поля msg_id, inn, full_name
 * 3. Сохранить клиента в PostgreSQL
 * 4. Отправить подтверждение в топик message-out
 */
@Component
public class KafkaMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private final ObjectMapper objectMapper;
    private final ClientService clientService;
    private final KafkaMessageProducer kafkaMessageProducer;

    public KafkaMessageConsumer(
            ObjectMapper objectMapper,
            ClientService clientService,
            KafkaMessageProducer kafkaMessageProducer) {
        this.objectMapper = objectMapper;
        this.clientService = clientService;
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    /**
     * Вызывается автоматически при появлении нового сообщения в topic1.
     * Параметр payload — это value записи Kafka (строка с JSON).
     */
    @KafkaListener(topics = "${app.kafka.input-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) {
        log.info("Received message from topic1: {}", payload);

        try {
            InboundMessage message = objectMapper.readValue(payload, InboundMessage.class);
            validate(message);

            Client savedClient = clientService.saveFromMessage(message);
            kafkaMessageProducer.sendRecordSaved(savedClient.getId(), savedClient.getTime());

            log.info("Saved client id={}, msgId={}", savedClient.getId(), savedClient.getMsgId());
        } catch (Exception e) {
            log.error("Failed to process message: {}", payload, e);
            throw new IllegalStateException("Message processing failed", e);
        }
    }

    /** Проверяет наличие и корректность обязательных полей входящего сообщения. */
    private void validate(InboundMessage message) {
        if (message.getMsgId() == null || message.getMsgId().isBlank()) {
            throw new IllegalArgumentException("msg_id is required");
        }
        try {
            java.util.UUID.fromString(message.getMsgId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("msg_id must be a valid UUID: " + message.getMsgId(), e);
        }
        if (message.getInn() == null || message.getInn().isBlank()) {
            throw new IllegalArgumentException("inn is required");
        }
        if (message.getFullName() == null || message.getFullName().isBlank()) {
            throw new IllegalArgumentException("full_name is required");
        }
    }
}
