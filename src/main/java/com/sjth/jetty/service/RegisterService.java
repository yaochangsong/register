/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sjth.jetty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sjth.jetty.model.req.JDRegReq;
import com.sjth.jetty.model.resp.JDRegData;
import com.sjth.jetty.model.resp.RegisterResp;
import com.sjth.jetty.pojo.RegisterBody;
import com.sjth.jetty.util.DataMsg;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RegisterService {

    private final Logger LOG = LoggerFactory.getLogger(RegisterService.class);

    @Resource
    private DefaultMQProducer producer;
    @Resource
    private StringRedisTemplate mqttRedis;
    @Resource
    private StringRedisTemplate jdRedis;

    @Value("${rocketMQ.mqtt.topicName}")
    private String mqttTopic;
    @Value("${rocketMQ.mqtt.tagName}")
    private String mqttTag;

    @Value("${rocketMQ.jd.topicName}")
    private String jdTopic;
    @Value("${rocketMQ.jd.tagName}")
    private String jdTag;

    public RegisterResp register(RegisterBody reg) {
        RegisterResp ret = null;
        if (null != reg && !StringUtils.isEmpty(reg.getDeviceName()) && reg.getType() > 0 && reg.getType() < 4) {
            // 先从redis中判定该设备的注册信息是否存在
            if (mqttRedis.hasKey(reg.getDeviceName())) {
                String regData = mqttRedis.opsForValue().get(reg.getDeviceName());
                if (!StringUtils.isEmpty(regData)) {
                    ret = JSONObject.parseObject(regData, RegisterResp.class);
                }
            } else {// 不存在，说明为设备第一次进行注册申请
                // 先往redis中插入一条该设备的注册信息，但是注册信息为空，这样做是为了避免一个设备的注册消息发送多次
                mqttRedis.opsForValue().set(reg.getDeviceName(), "", 5, TimeUnit.MINUTES);
                // 通知mcs服务注册该设备，此消息由mcs服务进行消费然后进行注册
                registerDevice(JSON.toJSONString(reg), mqttTopic, mqttTag);
            }
        }
        return ret;
    }

    public JDRegData register(String regReqStr){
        JDRegData ret = null;
        JDRegReq req = JSON.parseObject(regReqStr, JDRegReq.class);
        if (null != req && !StringUtils.isEmpty(req.getArgs().getHardSearial())
                && !StringUtils.isEmpty(req.getArgs().getWiredMac())) {

            // 先从redis中判定该设备的注册信息是否存在
            String searchKey = req.getArgs().getWiredMac().concat("*");
            Set<String> keys = jdRedis.keys(searchKey);
            if (null != keys && !keys.isEmpty()){
                String key = keys.stream().findFirst().get();
                String regData = jdRedis.opsForValue().get(key);
                if (!StringUtils.isEmpty(regData)) {
                    ret = JSONObject.parseObject(regData, JDRegData.class);
                }
            } else { // 不存在，说明为设备第一次进行注册申请
                // 先往redis中插入一条该设备的注册信息，但是注册信息为空，这样做是为了避免一个设备的注册消息发送多次
                jdRedis.opsForValue().set(req.getArgs().getWiredMac(), "", 5, TimeUnit.MINUTES);
                // 通知mcs服务注册该设备，此消息由mcs服务进行消费然后进行注册
                registerDevice(regReqStr, jdTopic, jdTag);
            }
        }
        return ret;
    }

    public DataMsg initJD(){
        DataMsg ret = new DataMsg();
        int[] count = {0};
        jdRedis.keys("*").forEach(key -> {
            String regInfo = jdRedis.opsForValue().get(key);
            if (!StringUtils.isEmpty(regInfo)){
                JDRegData regData = JSON.parseObject(regInfo, JDRegData.class);
                String newKey = key.substring(0,key.lastIndexOf(':') + 3).concat(regData.getDeviceUuid());
                jdRedis.delete(key);
                jdRedis.opsForValue().set(newKey, regInfo);
                count[0]++;
            }
        });
        ret.setCode(0);
        ret.setMessage("init count: " + count[0]);
        return ret;
    }

    private void registerDevice(String msg, String topicName, String tagName) {
        try {
            Message message = new Message(topicName, tagName, msg.getBytes());
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    LOG.info("send register info to topic:{} tag:{} success", topicName, tagName);
                }

                @Override
                public void onException(Throwable throwable) {
                    LOG.error("send register info failed: {}", throwable.getMessage());
                }
            },1000);
        } catch (InterruptedException | RemotingException | MQClientException e) {
            LOG.error("send device register info failed，because：{}, message: {}", e.getCause(), e.getMessage());
        }
    }
}
