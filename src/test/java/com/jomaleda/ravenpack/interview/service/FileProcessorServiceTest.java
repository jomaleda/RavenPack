package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.dto.InputMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FileProcessorServiceTest {

    private FileProcessorService fileProcessorService;
    
    @Mock
    private CsvService csvService;
    
    @Mock
    private TranslationService translationService;
    
    @Mock
    private ScoringService scoringService;
    
    private ExecutorService taskExecutor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskExecutor = Executors.newVirtualThreadPerTaskExecutor();
        fileProcessorService = new FileProcessorService(csvService, translationService, scoringService, taskExecutor);
    }

    @Test
    void processFile_ValidInput_ProcessesSuccessfully() throws IOException {
        String inputPath = "data/input.csv";
        String outputPath = "data/output.csv";
        
        InputMessage message1 = new InputMessage("user1", "hello");
        InputMessage message2 = new InputMessage("user2", "world");
        Stream<InputMessage> messageStream = Stream.of(message1, message2);
        
        when(csvService.readMessages(inputPath)).thenReturn(messageStream);
        when(translationService.translate("hello")).thenReturn("olleh");
        when(translationService.translate("world")).thenReturn("dlrow");
        when(scoringService.getScore("olleh")).thenReturn(0.5f);
        when(scoringService.getScore("dlrow")).thenReturn(0.7f);
        
        assertDoesNotThrow(() -> fileProcessorService.processFile(inputPath, outputPath));
        
        verify(csvService).readMessages(inputPath);
        verify(csvService).writeReports(eq(outputPath), any(List.class));
        verify(translationService, times(2)).translate(anyString());
        verify(scoringService, times(2)).getScore(anyString());
    }

    @Test
    void processFile_IOException_ThrowsRuntimeException() throws IOException {
        String inputPath = "data/nonexistent.csv";
        String outputPath = "data/output.csv";
        
        when(csvService.readMessages(inputPath)).thenThrow(new IOException("File not found"));
        
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> fileProcessorService.processFile(inputPath, outputPath));
        
        assertEquals("Failed to read input file", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void processFile_EmptyStream_CompletesSuccessfully() throws IOException {
        String inputPath = "data/empty.csv";
        String outputPath = "data/output.csv";
        
        when(csvService.readMessages(inputPath)).thenReturn(Stream.empty());
        
        assertDoesNotThrow(() -> fileProcessorService.processFile(inputPath, outputPath));
        
        verify(csvService).readMessages(inputPath);
        verify(csvService).writeReports(eq(outputPath), any(List.class));
    }

    @Test
    void processFile_ServiceException_ContinuesProcessing() throws IOException {
        String inputPath = "data/input.csv";
        String outputPath = "data/output.csv";
        
        InputMessage message1 = new InputMessage("user1", "hello");
        InputMessage message2 = new InputMessage("user2", "world");
        Stream<InputMessage> messageStream = Stream.of(message1, message2);
        
        when(csvService.readMessages(inputPath)).thenReturn(messageStream);
        when(translationService.translate("hello")).thenThrow(new RuntimeException("Translation failed"));
        when(translationService.translate("world")).thenReturn("dlrow");
        when(scoringService.getScore("dlrow")).thenReturn(0.7f);
        
        assertDoesNotThrow(() -> fileProcessorService.processFile(inputPath, outputPath));
        
        verify(csvService).writeReports(eq(outputPath), any(List.class));
    }

    @Test
    void processFile_MultipleUsers_AggregatesCorrectly() throws IOException {
        String inputPath = "data/input.csv";
        String outputPath = "data/output.csv";
        
        InputMessage message1 = new InputMessage("user1", "hello");
        InputMessage message2 = new InputMessage("user1", "world");
        InputMessage message3 = new InputMessage("user2", "test");
        Stream<InputMessage> messageStream = Stream.of(message1, message2, message3);
        
        when(csvService.readMessages(inputPath)).thenReturn(messageStream);
        when(translationService.translate(anyString())).thenAnswer(i -> 
            new StringBuilder(i.getArgument(0).toString()).reverse().toString());
        when(scoringService.getScore(anyString())).thenReturn(0.5f);
        
        assertDoesNotThrow(() -> fileProcessorService.processFile(inputPath, outputPath));
        
        verify(csvService).writeReports(eq(outputPath), any(List.class));
        verify(translationService, times(3)).translate(anyString());
        verify(scoringService, times(3)).getScore(anyString());
    }
}