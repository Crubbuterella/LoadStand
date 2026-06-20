package com.example.kafkaclient.service;

import com.example.kafkaclient.dto.InboundMessage;
import com.example.kafkaclient.model.Client;
import com.example.kafkaclient.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Логика сохранения клиента в PostgreSQL. */
@Service
public class ClientService {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Создаёт запись в таблице clients на основе данных из Kafka-сообщения.
     * message - распарсенный JSON из топика topic1
     * return - сохраняемая сущность с присвоенным id
     */
    @Transactional
    public Client saveFromMessage(InboundMessage message) {
        Client client = new Client();
        client.setMsgId(UUID.fromString(message.getMsgId()));
        client.setInn(message.getInn());
        client.setFullName(message.getFullName());
        client.setTime(LocalDateTime.now().format(TIME_FORMATTER));

        return clientRepository.save(client);
    }
}
