package gov.cdc.usds.simplereport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;

@Configuration
// @EnableAsync
@Profile("!no-async")
public class AsyncConfiguration implements AsyncConfigurer {}
