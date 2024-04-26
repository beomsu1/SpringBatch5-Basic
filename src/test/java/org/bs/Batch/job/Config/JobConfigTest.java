package org.bs.Batch.job.Config;

import lombok.extern.log4j.Log4j2;
import org.bs.Batch.SpringBatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest(classes = {SpringBatchTestConfig.class, JobConfig.class})
@SpringBatchTest
class JobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @DisplayName("JobConfig Test")
    @Test
    public void jobConfigTest() throws Exception{

        // Given
        log.info("JobConfig Test Start");

        // When
       JobExecution execution = jobLauncherTestUtils.launchJob();

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        log.info("JobConfig Test Complete");
    }

}