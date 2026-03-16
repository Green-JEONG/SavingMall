package com.hana8.hanaro.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogEventPublisher {

    private static final Logger USER_LOGGER = LoggerFactory.getLogger("USER_LOGGER");
    private static final Logger PRODUCT_LOGGER = LoggerFactory.getLogger("PRODUCT_LOGGER");
    private static final Logger SERVICE_LOGGER = LoggerFactory.getLogger("SERVICE_LOGGER");

    public void user(String message) {
        USER_LOGGER.info(message);
    }

    public void product(String message) {
        PRODUCT_LOGGER.info(message);
    }

    public void service(String message) {
        SERVICE_LOGGER.info(message);
    }
}
