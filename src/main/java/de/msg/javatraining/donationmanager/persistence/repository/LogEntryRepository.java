package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
}
