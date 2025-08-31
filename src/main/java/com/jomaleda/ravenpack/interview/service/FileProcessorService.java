package com.jomaleda.ravenpack.interview.service;

import com.jomaleda.ravenpack.interview.dto.InputMessage;
import com.jomaleda.ravenpack.interview.dto.UserReport;
import com.jomaleda.ravenpack.interview.model.UserStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for processing CSV files containing user messages.
 * Orchestrates translation, scoring, and report generation using concurrent processing.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileProcessorService {
    private final CsvService csvService;
    private final TranslationService translationService;
    private final ScoringService scoringService;
    private final ExecutorService taskExecutor;

    /**
     * Processes a CSV file of user messages and generates a summary report.
     * Uses concurrent processing with virtual threads for high performance.
     *
     * @param inputPath path to input CSV file containing user messages
     * @param outputPath path where the output CSV report will be written
     * @throws RuntimeException if file processing fails
     */
    public void processFile(String inputPath, String outputPath) {
        Map<String, UserStats> userStatsMap = new ConcurrentHashMap<>();
        AtomicInteger errorCount = new AtomicInteger(0);

        try (Stream<InputMessage> messageStream = csvService.readMessages(inputPath)) {
            List<CompletableFuture<Void>> futures = messageStream
                    .map(message -> CompletableFuture.runAsync(() -> processMessage(message, userStatsMap, errorCount), taskExecutor))
                    .collect(Collectors.toList());

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);
            } catch (TimeoutException e) {
                log.error("Processing timed out after 5 minutes");
                throw new RuntimeException("File processing timed out", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("File processing was interrupted", e);
            }

        } catch (IOException e) {
            log.error("Failed to read input file: {}", inputPath, e);
            throw new RuntimeException("Failed to read input file", e);
        } catch (ExecutionException e) {
            log.error("Failed to execute message processing tasks", e);
            throw new RuntimeException("Failed to process messages", e);
        } catch (RuntimeException e) {
            log.error("Runtime error during file processing", e);
            throw e;
        }

        int totalErrors = errorCount.get();
        if (totalErrors > 0) {
            log.warn("Processing completed with {} errors. Check logs for details.", totalErrors);
        }
        
        log.info("Aggregation complete. Generating report for {} users...", userStatsMap.size());
        List<UserReport> reports = generateUserReports(userStatsMap);
        csvService.writeReports(outputPath, reports);
    }

    /**
     * Processes a single message by translating and scoring it.
     * Updates user statistics in a thread-safe manner.
     *
     * @param message the input message to process
     * @param userStatsMap concurrent map storing user statistics
     * @param errorCount atomic counter for tracking processing errors
     */
    private void processMessage(InputMessage message, Map<String, UserStats> userStatsMap, AtomicInteger errorCount) {
        try {
            String translatedMessage = translationService.translate(message.getMessage());
            float score = scoringService.getScore(translatedMessage);

            userStatsMap.compute(message.getUserId(), (userId, stats) -> {
                if (stats == null) {
                    return new UserStats(1, score);
                }
                stats.addMessage(score);
                return stats;
            });
        } catch (Exception e) {
            errorCount.incrementAndGet();
            log.error("Failed to process message for user {}: {}", message.getUserId(), message.getMessage(), e);
        }
    }

    /**
     * Generates user reports from aggregated statistics.
     *
     * @param userStatsMap map containing user statistics
     * @return list of user reports with total messages and average scores
     */
    private List<UserReport> generateUserReports(Map<String, UserStats> userStatsMap) {
        return userStatsMap.entrySet().stream()
                .map(entry -> new UserReport(
                        entry.getKey(),
                        entry.getValue().getTotalMessages(),
                        entry.getValue().getAverageScore()
                ))
                .collect(Collectors.toList());
    }
}