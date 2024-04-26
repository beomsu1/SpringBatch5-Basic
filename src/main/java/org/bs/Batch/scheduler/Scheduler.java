package org.bs.Batch.scheduler;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Scheduler {
    
    @Autowired
    private Job testJob; // Job 이름 설정
    
    @Autowired
    private JobLauncher jobLauncher;

    @Scheduled(cron = "0 */1 * * * *") // 1분마다
    public void testJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", LocalDateTime.now().toString())  // 현재 날짜와 시간을 문자열로 추가
                .toJobParameters();

        jobLauncher.run(testJob, jobParameters); // 실행할 Job, 파라미터
    }
}
