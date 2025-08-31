package com.jomaleda.ravenpack.interview;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ApplicationRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Override
    public void run(String... args) {
        logger.info("HELLO WORLD!");
    }
}