package com.studio.scout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Crucial: Runs multi-batch sleep timers on a separate background thread
public class PipelineEngineApplication {
	public static void main(String[] args) {
		SpringApplication.run(PipelineEngineApplication.class, args);
	}
}