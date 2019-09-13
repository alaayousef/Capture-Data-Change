package gov.eehc.holding.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author Mawaziny
 */
@EnableScheduling
@SpringBootApplication
public class CdcClientApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String... args) {
        SpringApplication.run(CdcClientApplication.class, args);
    }
}
