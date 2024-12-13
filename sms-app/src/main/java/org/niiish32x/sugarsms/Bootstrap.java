package org.niiish32x.sugarsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * bootstrap
 *
 * @author shenghao ni
 * @date 2024.12.08 13:21
 */

@EnableScheduling
@SpringBootApplication(scanBasePackages = "org.niiish32x.sugarsms.*")
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class);
    }
}
