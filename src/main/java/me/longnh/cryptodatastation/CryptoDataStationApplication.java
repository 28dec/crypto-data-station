package me.longnh.cryptodatastation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoDataStationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoDataStationApplication.class, args);
    }

}
