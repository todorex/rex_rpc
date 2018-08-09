package com.todorex.api.service;

import com.todorex.api.entitiy.Person;

/**
 * @Author rex
 * 2018/8/7
 */
public interface HelloService {

    String hello(String name);

    String hello(Person person);
}
