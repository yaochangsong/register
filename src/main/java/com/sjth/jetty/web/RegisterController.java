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

package com.sjth.jetty.web;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.sjth.jetty.model.req.JDRegReq;
import com.sjth.jetty.model.resp.JDRegData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.sjth.jetty.model.req.RegisterReq;
import com.sjth.jetty.model.resp.RegisterResp;
import com.sjth.jetty.pojo.RegisterBody;
import com.sjth.jetty.service.RegisterService;
import com.sjth.jetty.util.Constants;
import com.sjth.jetty.util.DataMsg;
import com.sjth.jetty.util.SignUtil;

@RestController
@RequestMapping("/device")
public class RegisterController {
    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Resource
    private RegisterService registerService;
    @Resource
    private SignUtil signUtil;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public DataMsg register(@RequestBody RegisterReq registerReq) {
        int code = Constants.FAILED;
        registerReq.setUrl("/device/register");
        RegisterBody registerBody = new RegisterBody();
        registerBody.setDeviceName(registerReq.getId());
        registerBody.setType(registerReq.getType());
        logger.info("receive dev register mqtt request：{}", JSON.toJSONString((registerReq)));
        // 认证
        if (!StringUtils.isEmpty(registerReq.getKeyId())) {
            // key 认证
            if (!signUtil.validateKey(registerReq.getKeyId())) {
                logger.error("keyId： {} is not available", registerReq.getKeyId());
                return new DataMsg<>(code);
            }

            // sign 认证
            if (!signUtil.validateSign(registerReq.getSign(), registerReq)) {
                logger.error("sign： {} is not available", registerReq.getSign());
                return new DataMsg<>(code);
            }
        }

        DataMsg<RegisterResp> msg = new DataMsg<>(Constants.FAILED);
        RegisterResp resp = this.registerService.register(registerBody);
        if (null != resp){
            msg = new DataMsg<>(Constants.SUCCESS);
            msg.setData(resp);
        }
        logger.info("response dev register mqtt info: {}", JSON.toJSONString(msg));
        return msg;
    }

    @RequestMapping(value = "/register/jd", method = RequestMethod.POST)
    public DataMsg registerJD(@RequestBody String jdRegReq){
        logger.info("receive dev register jd request: {}", jdRegReq);
        DataMsg<JDRegData> msg = new DataMsg<>(Constants.FAILED);
        JDRegData resp = this.registerService.register(jdRegReq);
        if (null != resp){
            msg = new DataMsg<>(Constants.SUCCESS);
            msg.setData(resp);
        }
        logger.info("response dev register jd info: {}", JSON.toJSONString(msg));
        return msg;
    }

    @GetMapping("/init/jdRegInfo")
    public DataMsg initJD(){
        return registerService.initJD();
    }
}