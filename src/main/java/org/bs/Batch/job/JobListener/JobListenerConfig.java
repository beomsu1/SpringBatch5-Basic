package org.bs.Batch.job.JobListener;

import lombok.extern.log4j.Log4j2;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/*
* desc: 스프링 배치 기본 구조 테스트
* run: --spring.batch.job.name=listenerJob <- JobBuilder로 생성된 이름 작성
* Job에 Listener 설정 추가
* */
@Log4j2
@Configuration
public class JobListenerConfig {

    // Job -> Step -> Tasklet

    // JobRepository -> 배치 작업의 메타데이터를 저장하는 데이터베이스, 배치 작업의 실행 상태, 실행 이력, 파라미터 등의 정보를 관리
    @Bean
    public Job listenerJob(JobRepository jobRepository, Step listenerStep) {
        return new JobBuilder("listenerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener()) //  리스너 추가
                .start(listenerStep)
                .build();
    }

    @JobScope
    @Bean
    public Step listenerStep(JobRepository jobRepository, Tasklet listenerTasklet, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("ListenerStep", jobRepository)
                .tasklet(listenerTasklet, platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet listenerTasklet() {
        return ((contribution, chunkContext) -> {
            log.info("Spring Batch Test, Listener Tasklet");
            return RepeatStatus.FINISHED;
        });
    }
}
