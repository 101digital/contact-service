package io.marketplace.services.contact;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.marketplace.commons.logging.EventCategory;
import io.marketplace.commons.logging.LogData;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;

@SpringBootApplication
public class SpringTemplateServiceApplication {
	
	 private static final Logger log = LoggerFactory.getLogger(SpringTemplateServiceApplication.class);

	 @Value("${spring.application.name}")
	 private static String applicationName;
	 
    public static void main(String[] args) {
        SpringApplication.run(SpringTemplateServiceApplication.class, args);
        log.trace(LogData.builder()
                .category(EventCategory.APPLICATION)                
                .title(applicationName + " succeesully started")                
                .build());
    }

    @PostConstruct
    private void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
    
   
}
