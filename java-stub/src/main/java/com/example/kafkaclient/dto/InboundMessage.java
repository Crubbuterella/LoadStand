package com.example.kafkaclient.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Структура JSON из value входящего Kafka-сообщения (топик topic1).
 * <p>
 * Пример:
 * {@code {"msg_id": "uuid", "inn": "123456789012", "full_name": "Иванов Иван"}}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InboundMessage {

    @JsonProperty("msg_id")
    @JsonAlias("msgId")
    private String msgId;

    private String inn;

    @JsonProperty("full_name")
    private String fullName;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
