package gov.cdc.usds.simplereport.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class ExecutorConfig extends AsyncConfigurerSupport {
  @Override
  @Bean
  public Executor getAsyncExecutor() {
    return new ContextAwarePoolExecutor();
  }
}
