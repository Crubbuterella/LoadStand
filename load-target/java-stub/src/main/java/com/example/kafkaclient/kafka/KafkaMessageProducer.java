package com.example.kafkaclient.kafka;

import com.example.kafkaclient.config.KafkaProperties;
import com.example.kafkaclient.dto.OutboundMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Отправка результата обработки в исходящий Kafka-топик message-out. */
@Component
public class KafkaMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    public KafkaMessageProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Публикует JSON с id записи и временем сохранения.
     * Сообщение отправляется только в value, key = null.
     */
    public void sendRecordSaved(Long id, String time) {
        OutboundMessage outboundMessage = new OutboundMessage(id, time);

        try {
            String payload = objectMapper.writeValueAsString(outboundMessage);
            kafkaTemplate.send(kafkaProperties.getOutputTopic(), null, payload);
            log.info("Sent message to {}: {}", kafkaProperties.getOutputTopic(), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbound message", e);
        }
    }
}
