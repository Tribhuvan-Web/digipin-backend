package com.hackathon.resolutionconsent.digipin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ImmuDBConfig {

    private static final Logger logger = LoggerFactory.getLogger(ImmuDBConfig.class);

    @Value("${immudb.enabled:false}")
    private boolean enabled;

    @Value("${immudb.host:localhost}")
    private String host;

    @Value("${immudb.port:3322}")
    private int port;

    @Value("${immudb.database:digipin_audit}")
    private String database;

    @Value("${immudb.username:immudb}")
    private String username;

    @Value("${immudb.password:immudb}")
    private String password;

    @PostConstruct
    public void init() {
        if (enabled) {
            logger.warn("⚠️  ImmuDB is enabled but client library is not configured.");
            logger.warn("📦 To use ImmuDB in production, add dependency: io.codenotary:immudb4j:1.0.0");
            logger.warn("🔧 ImmuDB Configuration: {}:{} - Database: {}", host, port, database);
            logger.warn("📝 Running in MOCK MODE - Audit logs stored in-memory");
        } else {
            logger.info("ℹ️  ImmuDB is disabled. Set immudb.enabled=true to enable tamper-proof audit storage.");
            logger.info("📝 Audit logs will be stored in-memory (development mode)");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }
}
