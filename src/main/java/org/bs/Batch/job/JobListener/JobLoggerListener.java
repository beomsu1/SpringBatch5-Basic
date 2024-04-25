package org.bs.Batch.job.JobListener;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Log4j2
public class JobLoggerListener implements JobExecutionListener {

    private static final String BEFORE_MESSAGE = "{} JOB is Running";
    private static final String AFTER_MESSAGE = "{} JOB is Done. (Status: {})";


    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(BEFORE_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(AFTER_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());

        if (jobExecution.getStatus() == BatchStatus.FAILED){
            // 필요에 따라서 결과를 받을 수 있게 구현

            log.info("Job is Fail");
        }
    }
}
