package org.bs.Batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
// @EnableBatchProcessing ---------- Spring Batch 5 부터는 사용 X
public class SpringBatchTestConfig {
}
