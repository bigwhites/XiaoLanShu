package com.ricky.blogserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class ExecutorConfig {

    @Bean(name = "tinyPool")
    VirtualThreadTaskExecutor tinyPool() {
        return new VirtualThreadTaskExecutor();
    }

//    @Bean(name = "heavyPool")
//    ThreadPoolTaskExecutor heavyExec() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        // 核心线程数
//        executor.setCorePoolSize(4);
//        // 最大线程数
//        executor.setMaxPoolSize(8);
//        // 队列最大长度
//        executor.setQueueCapacity(2000);
//        // 线程池维护线程所允许的空闲时间
//        executor.setKeepAliveSeconds(900);
//        // 线程前缀
//        executor.setThreadNamePrefix("AsyncExecutorThread-");
//        // 线程池对拒绝任务(无线程可用)的处理策略
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }
}
