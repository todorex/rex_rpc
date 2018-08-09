package com.todorex.sample.client;

import com.todorex.api.service.HelloService;
import com.todorex.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 调用方 String
 *
 * @Author rex
 * 2018/8/8
 */
public class HelloClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);

        String result = helloService.hello("world");
        System.out.println(result);

    }
}
