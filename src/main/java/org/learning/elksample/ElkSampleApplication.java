package org.learning.elksample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElkSampleApplication {

    private static String users[] = {"operez", "plopez", "mgutierrez", "zlara"};

    private static String cardBrand[] = {"VISA", "MASTERCARD", "AMEX"};

    private static String txnType[] = {"SALE", "REFUND"};

    private static final Logger logger = LoggerFactory.getLogger(ElkSampleApplication.class);

    public static void main(String[] args) {
        Random random = new Random();
        ExecutorService threads = Executors.newFixedThreadPool(10);

        for (int t = 0; t < 1000; t++) {
            threads.submit(() -> {
                for (int i = 0; i < 1000; i++) {

                    String idRequest = UUID.randomUUID().toString();
                    MDC.put("ID_REQUEST", idRequest);
                    MDC.put("USER", users[random.nextInt(users.length)]);
                    try {
                        MDC.put("IP_SOURCE", InetAddress.getLocalHost().getHostAddress());
                    } catch (UnknownHostException ex) {
                        logger.error("Can't get client IP", ex);
                    }

                    logger.info("Request received ");

                    try {
                        processRequest(random);
                    } catch (Exception ex) {
                        logger.error("Error processing request", ex);
                    }

                    logger.info("Request finished successfully");

                    sleepThread();
                }
            });
        }
    }

    private static void sleepThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void processRequest(Random random) {

        if (random.nextInt() % 2 == 0) {
            logger.info("Amount: {}, Card: {}, Type: {}", random.nextInt(10000), cardBrand[random.nextInt(cardBrand.length)],
                    txnType[random.nextInt(txnType.length)]);
        }

        if (random.nextInt() % 10 == 0) {
            throw new RuntimeException("Bad request");
        }

        if (random.nextInt() % 8 == 0) {
            throw new RuntimeException("User is not allowed");
        }

        if (random.nextInt() % 15 == 0) {
            throw new RuntimeException("User is not allowed");
        }

        if (random.nextInt() % 20 == 0) {
            throw new RuntimeException("Security risk");
        }

        if (random.nextInt() % 18 == 0) {
            int reason = random.nextInt() % 3;

            if (reason % 3 == 0) {
                logger.warn("Authentication service is not responding");
            } else if (reason % 3 == 1) {
                logger.warn("Bin information service is not available");
            } else {
                logger.warn("Card authorizer timeout");
            }

            throw new RuntimeException("Unavailable service");
        }

        try {
            Thread.sleep(random.nextInt(10000) + 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (random.nextInt() % 5 == 0) {
            logger.info("Denied Transaction");
        } else {
            logger.info("Approved Transaction");
        }
    }
}
