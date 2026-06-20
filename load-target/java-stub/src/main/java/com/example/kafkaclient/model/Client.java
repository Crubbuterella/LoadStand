package com.example.kafkaclient.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Сущность, соответствующая строке таблицы {@code clients} в PostgreSQL.
 */
@Entity
@Table(name = "clients")
public class Client {

    /** Автоинкрементный первичный ключ записи в БД. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор сообщения из Kafka (поле {@code msg_id} в JSON).
     * В БД столбец называется {@code msgId} и имеет тип UUID.
     * Кавычки в имени столбца нужны из-за camelCase в PostgreSQL.
     */
    @Column(name = "\"msgId\"", nullable = false)
    private UUID msgId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String inn;

    /** Время сохранения записи в формате {@code yyyy-MM-dd HH:mm}. */
    @Column
    private String time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getMsgId() {
        return msgId;
    }

    public void setMsgId(UUID msgId) {
        this.msgId = msgId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
