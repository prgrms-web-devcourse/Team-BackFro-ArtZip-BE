package com.prgrms.artzip.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.rmi.ServerException;
import java.util.Objects;

@Configuration
@Profile("!dev")
public class EmbeddedRedisConfig {
    //TODO: 내장 Redis Port 자동 설정
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String BIN_SH = "/bin/sh";
    private static final String BIN_SH_OPTION = "-c";
    private static final String COMMAND = "netstat -nat | grep LISTEN|grep %d";

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.host}")
    private String host;

    private RedisServer redisServer;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @PostConstruct
    public void redisServer() throws IOException, URISyntaxException {
        int redisPort = isRedisRunning() ? findAvailablePort() : port;
        redisServer = new RedisServer(redisPort);
        log.info("host: {}, port: {}", host, port);
        if (isArmMac()) {
            redisServer = new RedisServer(Objects.requireNonNull(getRedisFileForArmMac()), redisPort);
        }
        else {
            redisServer = new RedisServer(redisPort);
        }
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(port));
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
        }

        return !pidInfo.toString().isEmpty();
    }

    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format(COMMAND, port);
        String[] shell = {BIN_SH, BIN_SH_OPTION, command};
        return Runtime.getRuntime().exec(shell);
    }

    public int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }

        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    private boolean isArmMac() {
        return Objects.equals(System.getProperty("os.arch"), "aarch64") && Objects.equals(System.getProperty("os.name"), "Mac OS X");
    }

    private File getRedisFileForArmMac() throws ServerException {
        try{
            return new ClassPathResource("binary/redis/redis-server-7.0.4-mac-arm64").getFile();
        } catch (Exception e) {
            throw new ServerException("Embedded Redis Server run failed");
        }
    }
}
