package com.hana8.hanaro.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogEventPublisher {

    private static final Logger APP_LOGGER = LoggerFactory.getLogger(LogEventPublisher.class);
    private static final Logger USER_LOGGER = LoggerFactory.getLogger("audit.user");
    private static final Logger PRODUCT_LOGGER = LoggerFactory.getLogger("audit.product");
    private static final Logger SERVICE_LOGGER = LoggerFactory.getLogger("audit.service");
    private static final Logger SUBSCRIPTION_LOGGER = LoggerFactory.getLogger("audit.subscription");

    public void application(String message) {
        APP_LOGGER.info(message);
    }

    public void user(String message) {
        USER_LOGGER.info(message);
    }

    public void product(String message) {
        PRODUCT_LOGGER.info(message);
    }

    public void service(String message) {
        SERVICE_LOGGER.info(message);
    }

    public void subscription(String message) {
        SUBSCRIPTION_LOGGER.info(message);
    }
}
