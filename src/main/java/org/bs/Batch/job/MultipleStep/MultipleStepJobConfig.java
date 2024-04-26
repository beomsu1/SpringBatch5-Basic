package org.bs.Batch.job.MultipleStep;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/*
* desc: 여러개의 Step 사용, step to step 데이터 전달
* run: --spring.batch.job.name="MultipleStepJob"
* */
@Log4j2
@Configuration
public class MultipleStepJobConfig {

    @Bean
    public Job multipleStepJob(JobRepository jobRepository, Step multipleStep1, Step multipleStep2, Step multipleStep3){
        return new JobBuilder("MultipleStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multipleStep1)
                .next(multipleStep2)
                .next(multipleStep3)
                .build();
    }

    @Bean
    @JobScope
    public Step multipleStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("MultipleStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("MultipleStep1");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    // ExecutionContext에 데이터를 담아서 Step으로 보내주고 다음 Step으로 받아서 사용 (MultipleStep2, Step3)

    // Step으로 데이터 보내기 context.put()
    @Bean
    @JobScope
    public Step multipleStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("MultipleStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("MultipleStep2");

                    // Context 가져오기
                    ExecutionContext context = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext(); // 배치 작업 실행 동안 데이터를 유지하고 전달하는 데 사용

                    context.put("key", "value");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    // Step에서 보낸 데이터 받기 context.get();
    @Bean
    @JobScope
    public Step multipleStep3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("MultipleStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("MultipleStep3");

                    // Context 가져오기
                    ExecutionContext context = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    log.info(context.get("key"));
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

}
