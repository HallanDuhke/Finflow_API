package finflow.Finflow_API;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("demo") 
class FinflowApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
