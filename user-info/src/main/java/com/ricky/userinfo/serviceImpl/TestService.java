package com.ricky.userinfo.serviceImpl;
import com.ricky.apicommon.service.ITestService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

@DubboService
public class TestService implements ITestService {

    @Value("{spring.application.name}")
    private static Integer _port;
    @Override
    public String getPort() {
        System.out.println(_port);
        return String.valueOf(88881);
    }
}
