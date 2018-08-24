package com.sjth.jetty.config;

import com.sjth.jetty.model.resp.JDRegAiads;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

@Configuration
public class RocketMqConfig {

    private final Logger LOG = LoggerFactory.getLogger(RocketMqConfig.class);

    @Value("${rocketMQ.producer}")
    private String producerName;
    @Value("${rocketMQ.consumer}")
    private String consumerName;
    @Value("${rocketMQ.nameService}")
    private String nameService;

    @Bean
    @ConfigurationProperties(prefix = "adv.aiads")
    public JDRegAiads aiads(){
        return new JDRegAiads();
    }

    @Bean
    public DefaultMQProducer producer(){
        DefaultMQProducer producer = new DefaultMQProducer(producerName);
        producer.setNamesrvAddr(nameService);
        producer.setInstanceName(UUID.randomUUID().toString());
        producer.setRetryTimesWhenSendAsyncFailed(5);
        try{
            producer.start();
            LOG.info("register rocketMQ init success");
        } catch (MQClientException e){
            LOG.error("register mq init failed, errorMsg: {}, errorCode: {}", e.getErrorMessage(), e.getResponseCode());
        }
        return producer;
    }

    @Bean
    public DefaultMQPushConsumer consumer(){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerName);
        consumer.setNamesrvAddr(nameService);
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeThreadMax(6);
        consumer.setConsumeThreadMin(3);
        return consumer;
    }

    @Bean
    public RestTemplate okhttpTemplate(){
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(20000);
        factory.setWriteTimeout(20000);
        return new RestTemplate(factory);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public JedisPoolConfig redisPoolConfig(){
        return new JedisPoolConfig();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.mqttRedis")
    public JedisConnectionFactory mqttConnectionFactory(){
        JedisConnectionFactory state = new JedisConnectionFactory();
        state.setPoolConfig(redisPoolConfig());
        return state;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.jdRedis")
    public JedisConnectionFactory jdConnectionFactory(){
        JedisConnectionFactory bad = new JedisConnectionFactory();
        bad.setPoolConfig(redisPoolConfig());
        return bad;
    }

    @Bean
    public StringRedisTemplate mqttRedis(){
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(mqttConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate jdRedis(){
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jdConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
