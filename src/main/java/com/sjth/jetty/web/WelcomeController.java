package com.sjth.jetty.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Bone on 2017/10/18.
 */
@Controller
public class WelcomeController {
    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "hello";
    }

}
