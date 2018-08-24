package com.sjth.jetty.config;

import com.sjth.jetty.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class StartRocketMQ {

    private final Logger LOG = LoggerFactory.getLogger(StartRocketMQ.class);

    @Resource
    private ConsumerService rocketMQConsumerService;

    @PostConstruct
    public void onApplicationEvent() {
        new Thread(new RunRocketMQ()).start();
    }

    class RunRocketMQ implements Runnable{

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(5);
                rocketMQConsumerService.startConsumer();
            } catch (InterruptedException e) {
                LOG.info("start rocketMQ consumer failed: {}", e.getCause());
            }
        }
    }
}
