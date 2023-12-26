package com.example.demo.member.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.properties.mail.transport.protocol}")
    private String protocol;

    @Value("${mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${mail.properties.mail.smtp.socketFactory.class}")
    private String classProp;

    @Value("${mail.properties.mail.smtp.starttls.enable}")
    private String starttls;

    @Value("${mail.properties.mail.debug}")
    private String debug;

    @Value("${mail.properties.mail.smtp.ssl.trust}")
    private String trust;

    @Value("${mail.properties.mail.smtp.ssl.protocols}")
    private String protocols;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", protocol);
        javaMailProperties.put("mail.smtp.auth", auth);
        javaMailProperties.put("mail.smtp.socketFactory.class", classProp);
        javaMailProperties.put("mail.smtp.starttls.enable", starttls);
        javaMailProperties.put("mail.debug", debug);
        javaMailProperties.put("mail.smtp.ssl.trust", trust);
        javaMailProperties.put("mail.smtp.ssl.protocols", protocols);

        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}
