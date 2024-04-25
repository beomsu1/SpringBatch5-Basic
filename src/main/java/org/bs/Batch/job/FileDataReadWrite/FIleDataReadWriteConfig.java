package org.bs.Batch.job.FileDataReadWrite;

import lombok.extern.log4j.Log4j2;
import org.bs.Batch.job.FileDataReadWrite.dto.Player;
import org.bs.Batch.job.FileDataReadWrite.dto.PlayerYear;
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
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.io.Writer;

/*
 * desc: 파일 읽고 쓰기
 * run: --spring.batch.job.name=FileDataReadWriteJob
 * */
@Log4j2
@Configuration
public class FIleDataReadWriteConfig {

    @Bean
    public Job fileDataReadWriteJob(JobRepository jobRepository, Step fileDataReadWriteStep) {
        return new JobBuilder("FileDataReadWriteJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fileDataReadWriteStep)
                .build();
    }

    @Bean
    @JobScope
    public Step fileDataReadWriteStep(JobRepository jobRepository, ItemReader<Player> playerFlatFileItemReader,
                                      ItemProcessor<Player, PlayerYear> playerItemProcessor,
                                      ItemWriter<PlayerYear> playerFlatFileItemWriter,
                                      PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("FileDataReadWriteStep", jobRepository)
                .<Player, PlayerYear>chunk(5, platformTransactionManager)
                .reader(playerFlatFileItemReader)
                /* 데이터 들어오는지 확인
                .writer(new ItemWriter<Player>() {
                    @Override
                    public void write(Chunk<? extends Player> chunks) throws Exception {
                        chunks.forEach(log::info);
                    }
                })
                 */
                .processor(playerItemProcessor)
                .writer(playerFlatFileItemWriter)
                .build();
    }

    // 파일을, 기준으로 나눠서 객체로 변환
    @Bean
    @StepScope
    public FlatFileItemReader<Player> playerFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("PlayerFlatFileItemReader")
                .resource(new FileSystemResource("Players.csv")) // 파일 경로
                .lineTokenizer(new DelimitedLineTokenizer()) // 데이터를 어떤 기준으로 나눠줄지 설정
                .fieldSetMapper(new PlayerFieldSetMapper()) // 읽어온 데이터를 객체로 변환
                .linesToSkip(1) // 첫째줄 스킵
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Player, PlayerYear> playerItemProcessor() {
        return new ItemProcessor<Player, PlayerYear>() {
            @Override
            public PlayerYear process(Player item) throws Exception {
                return new PlayerYear(item);
            }
        };
    }

    // 읽어들인 데이터를 파일로 내보내기
    @Bean
    @StepScope
    public FlatFileItemWriter<PlayerYear> playerFlatFileItemWriter() {
        BeanWrapperFieldExtractor<PlayerYear> fieldExtractor = new BeanWrapperFieldExtractor<>(); // 어떤 필드를 사용할 지 명시
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearExperience"}); // 객체의 특정 속성들을 지정하고 추출
        fieldExtractor.afterPropertiesSet(); // 속성들이 바르게 설정되었는지 확인, 초기화 보장

        // 어떤 기준으로 파일을 만들어줄 지
        DelimitedLineAggregator<PlayerYear> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(","); // ,로 구분
        delimitedLineAggregator.setFieldExtractor(fieldExtractor); // 설정한 PlayerYear 객체에서 필드값 추출

        FileSystemResource fileSystemResource = new FileSystemResource("players_.txt"); // 저장할 위치, 파일 이름

        return new FlatFileItemWriterBuilder<PlayerYear>()
                .name("PlayerFlatFileItemWriter")
                .resource(fileSystemResource)
                .lineAggregator(delimitedLineAggregator) // 구분자 설정
                .headerCallback(new FlatFileHeaderCallback() { // 첫째줄에 헤더 생성 ("ID,lastName,position,yearExperience")
                    @Override
                    public void writeHeader(Writer writer) throws IOException {
                        writer.write("ID,lastName,position,yearExperience");
                    }
                })
                .build();
    }
}
