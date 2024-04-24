package org.bs.Batch.job.ValidatedParam;

import lombok.extern.log4j.Log4j2;
import org.bs.Batch.job.ValidatedParam.Validator.FileParamValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/*
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.name=ValidatedParamJob fileName=test.csv,  주의! -filName=~~ 으로 하면 null 뜸
 *
 * 스프링배치5.0 부터는 StepBuilderFactory, JobBuilderFactory는 사용 X
 * JobRepository로 명시적 표시 해줘야함.
 * */
@Log4j2
@Configuration
public class ValidatedParamJobConfig {

    // Job -> Step -> Tasklet

    // JobRepository -> 배치 작업의 메타데이터를 저장하는 데이터베이스, 배치 작업의 실행 상태, 실행 이력, 파라미터 등의 정보를 관리

    @Bean
    public Job validatedParamJob(JobRepository jobRepository, Step validatedParamStep) {
        return new JobBuilder("ValidatedParamJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 순차적으로 ID 부여
                //.validator(new FileParamValidator()) // Tasklet에서 검증을 안하고 Job에서 검증
                .validator(new FileParamValidator().MultiValidator())
                .start(validatedParamStep)
                .build();
    }

    @JobScope
    @Bean
    public Step validatedParamStep(JobRepository jobRepository, Tasklet validatedParamTasklet, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("ValidatedParamStep", jobRepository)
                .tasklet(validatedParamTasklet, platformTransactionManager)
                .build();
    }

    @StepScope // Step 실행 시에 빈을 생성하도록 지정 -> Bean의 생성 시점을 지정된 Scope가 실행되는 시점으로 지연
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) { // 파라미터로 파일 이름 전달
        return ((contribution, chunkContext) -> {
            log.info("fileName: " + fileName);
            log.info("Validated Param Tasklet");
            return RepeatStatus.FINISHED; // 작업이 끝나면 FINISHED
        });
    }
}
