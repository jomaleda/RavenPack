package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.dto.InputMessage;
import com.jomaleda.ravenpack.interview.dto.UserReport;
import com.opencsv.bean.CsvToBeanBuilder;
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
            var csvToBean = new CsvToBeanBuilder<InputMessage>(reader)
                    .withType(InputMessage.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            return csvToBean.stream().toList().stream();
        }
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
            StatefulBeanToCsv<UserReport> beanToCsv = new StatefulBeanToCsvBuilder<UserReport>(writer).build();
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
        Path path = Paths.get(filePath).normalize();
        if (!path.startsWith(allowedDirectory)) {
            throw new SecurityException("Invalid file path");
        }
        return path;
    }
}