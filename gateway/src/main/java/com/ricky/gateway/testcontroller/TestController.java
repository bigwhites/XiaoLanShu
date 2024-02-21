package com.ricky.gateway.testcontroller;

import com.ricky.apicommon.service.ITestService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @DubboReference(check = false)
    ITestService testService;

    @GetMapping("UserInfoPort")
    public String getPort(){
        return testService.getPort();
    }
}
