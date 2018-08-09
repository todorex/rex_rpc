package com.todorex.sample.server;

import com.todorex.api.entitiy.Person;
import com.todorex.api.service.HelloService;
import com.todorex.server.RpcService;

/**
 * @Author rex
 * 2018/8/8
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(String name) {
        return "hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
