package org.bs.Batch;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
@Log4j2
class BatchApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private DataSource dataSource;

	@Test
	public void dbConnectionTest(){

		try {
			dataSource.getConnection();
			
			log.info("연결 완료");
			
		}catch (SQLException e){
			e.printStackTrace();;
		}
	}

}
