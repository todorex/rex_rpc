package com.todorex.client;

import com.todorex.common.bean.RpcRequest;
import com.todorex.common.bean.RpcResponse;
import com.todorex.common.util.StringUtil;
import com.todorex.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Rpc代理（用于创建RPC服务代理）
 *
 * @Author rex
 * 2018/8/8
 */
@Slf4j
public class RpcProxy {

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass,"");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final  Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建Rpc请求对象并设置请求属性
                        RpcRequest request = RpcRequest.builder()
                                .requestId(UUID.randomUUID().toString())
                                .interfaceName(method.getDeclaringClass().getName())
                                .serviceVersion(serviceVersion)
                                .methodName(method.getName())
                                .parameterTypes(method.getParameterTypes())
                                .parameters(args)
                                .build();
                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName();
                            if (StringUtils.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            log.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }
                        if (StringUtils.isEmpty(serviceAddress)) {
                            throw  new RuntimeException("server address is not exist");
                        }
                        // 从RPC服务地址中解析主机名和端口号
                        String[] addressArray = StringUtil.split(serviceAddress, ":");
                        String host = addressArray[0];
                        int port = Integer.valueOf(addressArray[1]);
                        // 创建RPC客户端并发送RPC请求
                        RpcClient client = new RpcClient(host, port);
                        long startTime = System.currentTimeMillis();
                        RpcResponse response = client.send(request);
                        log.debug("request spend {}ms", System.currentTimeMillis() - startTime);
                        if (response == null) {
                            throw new RuntimeException("response is null");
                        }
                        // 返回RPC结果
                        if (response.getException() != null) {
                            throw response.getException();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
}
