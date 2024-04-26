package org.bs.Batch.job.DbDataReadWrite;

import lombok.extern.log4j.Log4j2;
import org.bs.Batch.SpringBatchTestConfig;
import org.bs.Batch.domain.orders.Orders;
import org.bs.Batch.repository.AccountsRepository;
import org.bs.Batch.repository.OrdersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class})
@SpringBatchTest
class TrMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @DisplayName("trMigrationConfig Test")
    @Test
    public void trMigrationConfigTest() throws Exception {

        // Given
        log.info("trMigrationConfig Test Start");

        Orders order1 = Orders.builder()
                .id(null)
                .orderItem("초콜릿")
                .price(1000)
                .orderDate(new Date())
                .build();

        ordersRepository.save(order1);

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        log.info(accountsRepository.count());
        log.info("trMigrationConfig Test Complete");
    }
}