package com.todorex.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 服务注解（标注在服务实现类上）
 *
 * @Author rex
 * 2018/8/8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 类的字节码
     * @return
     */
    Class<?> value();

    /**
     * 版本信息
     * @return
     */
    String version() default "";
}
