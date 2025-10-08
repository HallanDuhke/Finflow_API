package finflow.Finflow_API;

import org.springframework.boot.SpringApplication;

public class TestFinflowApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(FinflowApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
