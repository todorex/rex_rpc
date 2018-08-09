package com.todorex.sample.client;

import com.todorex.api.entitiy.Person;
import com.todorex.api.service.HelloService;
import com.todorex.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 调用方 Person
 * @Author rex
 * 2018/8/9
 */
public class HelloClient1 {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);

        Person person = Person.builder().firstName("li").lastName("xiang").build();
        String result = helloService.hello(person);
        System.out.println(result);

    }
}
