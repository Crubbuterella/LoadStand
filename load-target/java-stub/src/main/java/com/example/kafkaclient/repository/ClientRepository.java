package com.example.kafkaclient.repository;

import com.example.kafkaclient.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/** Репозиторий Spring Data JPA для сохранения и чтения записей таблицы clients. */
public interface ClientRepository extends JpaRepository<Client, Long> {
}
