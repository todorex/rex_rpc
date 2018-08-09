package com.todorex.sample.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动服务
 *
 * @Author rex
 * 2018/8/8
 */
@Slf4j
public class RpcBootstrap {
    public static void main(String[] args) {
        log.debug("start server");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
