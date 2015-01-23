package com.sungung;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // exit code will trigger batch
        System.exit(SpringApplication.exit(
                SpringApplication.run(Application.class, args)
        ));
    }
}
