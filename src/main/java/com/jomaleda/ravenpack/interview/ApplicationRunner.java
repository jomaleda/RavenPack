package com.jomaleda.ravenpack.interview;

import com.jomaleda.ravenpack.interview.service.FileProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationRunner implements CommandLineRunner {
    private final FileProcessorService fileProcessorService;

    @Override
    public void run(String... args) {
        if (args.length != 2) {
            log.error("Usage: java -jar <jar-file-name>.jar <input-csv-path> <output-csv-path>");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        log.info("Starting content moderation process...");
        log.info("Input file: {}", inputPath);
        log.info("Output file: {}", outputPath);

        long startTime = System.currentTimeMillis();
        try {
            fileProcessorService.processFile(inputPath, outputPath);
            long endTime = System.currentTimeMillis();
            log.info("Process finished successfully in {}ms.", (endTime - startTime));
        } catch (SecurityException e) {
            log.error("Security error: Invalid file path provided - {}", e.getMessage());
            System.exit(2);
        } catch (RuntimeException e) {
            log.error("Processing error: {}", e.getMessage(), e);
            System.exit(3);
        }
    }
}