package ru.practicum.stat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
@EntityScan("ru.practicum.stat.model")
public class StatServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatServerApplication.class, args);
    }
}