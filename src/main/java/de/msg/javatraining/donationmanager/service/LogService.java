package de.msg.javatraining.donationmanager.service;

import de.msg.javatraining.donationmanager.persistence.model.LogEntry;
import de.msg.javatraining.donationmanager.persistence.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogService {

    @Autowired
    private LogEntryRepository logEntryRepository;

    public void logOperation(String operation, String message, String changedBy) {
        saveLog(operation, message, changedBy);
    }

    private void saveLog(String operation, String message, String changedBy) {
        LogEntry logEntry = new LogEntry();
        logEntry.setTimestamp(new Date());
        logEntry.setLevel(operation); // This will store the operation type (INSERT, UPDATE, DELETE, ERROR)
        logEntry.setMessage(message);
        logEntry.setChangedBy(changedBy); // This will always be null as per your request
        logEntryRepository.save(logEntry);
    }
}
