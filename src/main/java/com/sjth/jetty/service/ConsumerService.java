package com.sjth.jetty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sjth.jetty.model.req.JDRegReq;
import com.sjth.jetty.model.resp.JDRegAiads;
import com.sjth.jetty.model.resp.JDRegData;
import com.sjth.jetty.model.resp.JDRegResp;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class ConsumerService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    @Resource
    private DefaultMQPushConsumer consumer;
    @Resource
    private RestTemplate okhttpTemplate;
    @Resource
    private StringRedisTemplate jdRedis;
    @Resource
    private JDRegAiads aiads;

    @Value("${rocketMQ.jd.topicName}")
    private String jdTopic;
    @Value("${rocketMQ.jd.tagName}")
    private String jdTag;

    @Value("${adv.jdRegister}")
    private String jdRegister;
    @Value("${adv.userToken}")
    private String userToken;
    @Value("${adv.userName}")
    private String userName;

    public void startConsumer() {
        try {
            LOG.info("consumer listen topic name: {}, tag: {}", jdTopic, jdTag);
            consumer.subscribe(jdTopic, jdTag);
            // 从上次读取位置开始读取
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            // 消息分派处理
            consumer.registerMessageListener((MessageListenerOrderly) (list, context) -> {
                try {
                    context.setAutoCommit(true);
                    list.forEach(msg -> {
                        if (StringUtils.isNotBlank(msg.getTags())) {
                            byte[] msgBuf = msg.getBody();
                            String msgData = new String(msgBuf);
                            try {
                                JDRegReq regData = JSON.parseObject(msgData, JDRegReq.class);
                                regData.getArgs().setUsername(userName);
                                String mac = regData.getArgs().getWiredMac();

                                // 取出args json对象
                                String args = JSON.toJSONString(regData.getArgs(), SerializerFeature.SortField);
                                // 计算token
                                String token = DigestUtils.md5Hex(userName.toLowerCase().concat(userToken.toLowerCase()));
                                // 使用args和token算出sign
                                String sign = DigestUtils.md5Hex(args.concat(token));

                                // 请求头
                                HttpHeaders headers = new HttpHeaders();
                                headers.add("Content-Type", "application/x-www-form-urlencoded");
                                // 请求参数
                                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                                params.add("args", args); // args设备参数
                                params.add("sign", sign); // 计算出来的sign
                                params.add("username", userName); // 京东给的注册用户名
                                HttpEntity<MultiValueMap> entity = new HttpEntity<>(params, headers);

                                // 发起请求并提交参数
                                ResponseEntity<JDRegResp> response = okhttpTemplate.exchange(jdRegister, HttpMethod.POST, entity, JDRegResp.class);
                                // 解析数据返回
                                if (null != response && response.getStatusCode() == HttpStatus.OK) {
                                    JDRegResp jdRegResp = response.getBody();
                                    if (null != jdRegResp && jdRegResp.getCode() == 0) {
                                        JDRegData resp = jdRegResp.getData();
                                        if (null != resp && StringUtils.isNotBlank(resp.getDeviceUuid()) && StringUtils.isNotBlank(resp.getImPassword()) &&
                                                StringUtils.isNotBlank(resp.getImUsername()) && StringUtils.isNotBlank(resp.getRsaPub()) &&
                                                StringUtils.isNotBlank(resp.getToken())) {
                                            resp.setAiads(aiads);
                                            String value = JSON.toJSONString(resp);
                                            jdRedis.opsForValue().set(mac.concat(resp.getDeviceUuid()), value);
                                            jdRedis.delete(mac);
                                            LOG.info("register jd SUCCESS: {}", value);
                                        } else {
                                            LOG.error("register jd return param loss: {}", JSON.toJSONString(resp));
                                        }
                                    } else {
                                        LOG.error("register jd failed: {}", JSON.toJSONString(jdRegResp));
                                    }
                                } else {
                                    LOG.error("register jd error: {}", JSON.toJSONString(response));
                                }
                                // 等待20毫秒再发送注册请求
                                TimeUnit.MILLISECONDS.sleep(20);
                            } catch (Exception e) {
                                LOG.error("register jd error, because: {}, message: {}", e.getCause(), e.getMessage());
                            }
                        }
                    });
                } catch (Exception ex) {
                    LOG.error("consumer message failed：" + ex.getMessage());
                }
                return ConsumeOrderlyStatus.SUCCESS;
            });
            consumer.start();
            LOG.info("init consumer success");
        } catch (MQClientException e) {
            LOG.error("start consumer failed, error msg: {}, error code: {}", e.getErrorMessage(), e.getResponseCode());
        }
    }
}
