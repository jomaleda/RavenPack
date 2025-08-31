package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.dto.InputMessage;
import com.jomaleda.ravenpack.interview.dto.UserReport;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for handling CSV file operations with security validation.
 * 
 * <p>This service provides secure CSV processing capabilities for the content moderation system.
 * All file operations are restricted to the 'data' directory to prevent path traversal attacks.
 * Uses OpenCSV library for parsing and generating CSV files with proper resource management.</p>
 */
@Service
public class CsvService {
    private final Path allowedDirectory = Paths.get("data");

    /**
     * Reads messages from a CSV file and returns them as a stream.
     * 
     * @param filePath the path to the input CSV file (must be within 'data' directory)
     * @return a stream of InputMessage objects parsed from the CSV
     * @throws IOException if file reading fails
     * @throws SecurityException if file path is outside allowed directory
     */
    public Stream<InputMessage> readMessages(String filePath) throws IOException {
        Path validatedPath = validatePath(filePath);
        try (var reader = Files.newBufferedReader(validatedPath)) {
            return reader.lines()
                    .skip(1) // Skip header
                    .map(this::parseLineManually)
                    .filter(msg -> msg.getUserId() != null && !msg.getUserId().trim().isEmpty())
                    .collect(java.util.stream.Collectors.toList())
                    .stream();
        }
    }
    
    /**
     * Parses a CSV line manually to handle malformed data where commas in message content are not properly quoted.
     * Splits on the first comma to separate user_id from message, treating everything after as message content.
     * 
     * @param line the CSV line to parse
     * @return InputMessage with parsed user_id and message, or empty message if no comma found
     */
    private InputMessage parseLineManually(String line) {
        int firstComma = line.indexOf(',');
        if (firstComma == -1) return new InputMessage(line.trim(), "");
        
        String userId = line.substring(0, firstComma).trim();
        String message = line.substring(firstComma + 1).trim();

        if (message.isEmpty()) return new InputMessage(userId, "");
        
        if (message.startsWith("\"") && message.endsWith("\"") && message.length() > 1) {
            message = message.substring(1, message.length() - 1);
        }
        
        return new InputMessage(userId, message);
    }

    /**
     * Writes user reports to a CSV file.
     * 
     * @param filePath the path where the output CSV file will be created (must be within 'data' directory)
     * @param reports the list of UserReport objects to write
     * @throws RuntimeException if file writing fails
     * @throws SecurityException if file path is outside allowed directory
     */
    public void writeReports(String filePath, List<UserReport> reports) {
        Path validatedPath = validatePath(filePath);
        try (Writer writer = new FileWriter(validatedPath.toFile())) {
            StatefulBeanToCsv<UserReport> beanToCsv = new StatefulBeanToCsvBuilder<UserReport>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(reports);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Failed to write output CSV file", e);
        }
    }


    /**
     * Validates and normalizes the file path to prevent path traversal attacks.
     * Ensures the path is within the allowed 'data' directory.
     * 
     * @param filePath the file path to validate
     * @return the normalized and validated Path object
     * @throws SecurityException if the path is outside the allowed directory
     */
    private Path validatePath(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize().toAbsolutePath();
            Path allowedAbsolute = allowedDirectory.toAbsolutePath().normalize();
            if (!path.startsWith(allowedAbsolute)) {
                throw new SecurityException("Invalid file path");
            }
            return path;
        } catch (Exception e) {
            throw new SecurityException("Invalid file path");
        }
    }
}