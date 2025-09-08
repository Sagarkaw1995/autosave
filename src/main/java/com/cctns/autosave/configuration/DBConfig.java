package com.cctns.autosave.configuration;


import com.cctns.autosave.utility.EncryptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    public DBConfig(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    private final EncryptionUtil encryptionUtil;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {


        /*
            Checking whether the password in application.properties is encrypted or not
        */
        if(encryptionUtil.isBase64Encoded(password))
            password = encryptionUtil.decrypt(password);

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .build();
    }
}
