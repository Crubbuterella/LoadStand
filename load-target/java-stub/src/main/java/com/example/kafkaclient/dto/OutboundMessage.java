package com.example.kafkaclient.dto;

/**
 * Структура JSON для исходящего Kafka-сообщения (топик message-out).
 * Отправляется после успешной записи клиента в БД.
 * Пример: {"id": 1, "time": "2026-02-12 15:06"}
 */
public class OutboundMessage {

    /** ID сохранённой строки в таблице clients. */
    private Long id;

    /** Время записи в БД. */
    private String time;

    public OutboundMessage() {
    }

    public OutboundMessage(Long id, String time) {
        this.id = id;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
