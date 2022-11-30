package gov.cdc.usds.simplereport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("no-async")
public class NonAsyncConfiguration {}
