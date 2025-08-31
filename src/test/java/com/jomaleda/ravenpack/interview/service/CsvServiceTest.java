package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.dto.InputMessage;
import com.jomaleda.ravenpack.interview.dto.UserReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CsvServiceTest {

    private CsvService csvService;
    private Path testInputFile;
    private Path testOutputFile;

    @BeforeEach
    void setUp() throws IOException {
        csvService = new CsvService();
        Files.createDirectories(Path.of("data"));
        testInputFile = Path.of("data/test_input.csv");
        testOutputFile = Path.of("data/test_output.csv");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testInputFile);
        Files.deleteIfExists(testOutputFile);
    }

    @Test
    void readMessages_ValidFile_ReturnsMessages() throws IOException {
        String csvContent = "user_id,message\nuser1,Hello world\nuser2,Test message";
        Files.writeString(testInputFile, csvContent);

        List<InputMessage> messageList = csvService.readMessages("data/test_input.csv").toList();
        
        assertEquals(2, messageList.size());
        assertEquals("user1", messageList.get(0).getUserId());
        assertEquals("Hello world", messageList.get(0).getMessage());
        assertEquals("user2", messageList.get(1).getUserId());
        assertEquals("Test message", messageList.get(1).getMessage());
    }

    @Test
    void readMessages_InvalidPath_ThrowsSecurityException() {
        assertThrows(SecurityException.class, () -> 
            csvService.readMessages("../outside/file.csv"));
    }

    @Test
    void writeReports_ValidData_WritesFile() throws IOException {
        List<UserReport> reports = List.of(
            new UserReport("user1", 5, 3.2f),
            new UserReport("user2", 3, 4.1f)
        );

        csvService.writeReports("data/test_output.csv", reports);

        assertTrue(Files.exists(testOutputFile));
        String content = Files.readString(testOutputFile);
        assertTrue(content.contains("user1"));
    }

    @Test
    void writeReports_InvalidPath_ThrowsSecurityException() {
        List<UserReport> reports = List.of(new UserReport("user1", 1, 1.0f));
        
        assertThrows(SecurityException.class, () -> 
            csvService.writeReports("../outside/output.csv", reports));
    }

    @Test
    void validatePath_PathTraversal_ThrowsSecurityException() {
        assertThrows(SecurityException.class, () -> 
            csvService.readMessages("data/../../../etc/passwd"));
    }
}