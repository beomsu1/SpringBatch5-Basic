package org.bs.Batch.job.DbDataReadWrite;

import lombok.RequiredArgsConstructor;
import org.bs.Batch.domain.accounts.Accounts;
import org.bs.Batch.domain.orders.Orders;
import org.bs.Batch.repository.AccountsRepository;
import org.bs.Batch.repository.OrdersRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.List;

/*
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.name=TrMigrationJob
 * ItemReader , ItemProcessor, itemWriter 순서
 * ItemProcessor는 선택적으로 생략가능
 * */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(JobRepository jobRepository, Step trMigrationStep) {
        return new JobBuilder("TrMigrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step trMigrationStep(JobRepository jobRepository, ItemReader<Orders> trOrdersReader, ItemProcessor<Orders, Accounts> trOrderProcessor, ItemWriter<Accounts> trAccountsWriter, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("TrMigrationStep", jobRepository)
                .<Orders, Accounts>chunk(5, platformTransactionManager) // 어떤데이터로 불러와서 어떤 데이터로 사용할건지 작성, chunk 몇개의 단위로 처리할건지 명시
                .reader(trOrdersReader)
                .processor(trOrderProcessor)
                .writer(trAccountsWriter)
                .build();
    }

    // ItemReader는 read 메서드를 구현하여 하나의 아이템을 읽고 반환
    @Bean
    @StepScope
    public RepositoryItemReader<Orders> trOrdersReader() { // <읽을 타입> 작성
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll") // Repository의 findAll 메서드 사용
                .pageSize(5) // 일반적으로 청크사이즈랑 같게 설정
                .arguments(List.of()) // 인자를 리스트 형식으로
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();

    }


    // ItemProcessor는 process 메서드를 구현하여 하나의 아이템을 입력으로 받고, 가공된 결과를 반환
    @Bean
    @StepScope
    public ItemProcessor<Orders, Accounts> trOrderProcessor() { // Orders 객체 -> Accounts 객체로 반환
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }


    // ItemWriter는 write 메서드를 구현하여 가공된 데이터를 받아 처리

    // Repository를 이용한 writer 작업
    @Bean
    @StepScope
    public RepositoryItemWriter<Accounts> trRepositoryAccountsWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }

    // ItemWriter를 사용한 writer 작업
    @Bean
    @StepScope
    public ItemWriter<Accounts> trAccountsWriter() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(Chunk<? extends Accounts> chunks) throws Exception {
                chunks.forEach(chunk -> accountsRepository.save(chunk));
            }
        };
    }

}
