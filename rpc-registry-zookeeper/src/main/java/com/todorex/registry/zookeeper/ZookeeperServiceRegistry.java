package com.todorex.registry.zookeeper;

import com.todorex.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

/**
 * zookeeper 服务注册实现
 *
 * @Author rex
 * 2018/8/8
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private final String zkAddress;

    public ZookeeperServiceRegistry(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建Zookeeper客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        log.debug("connect zookeeper");
        // 创建 registry 节点(持久)
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点(持久)
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.debug("create service node: {}", servicePath);
        }
        // 创建 address节点(临时)
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        log.debug("create address node: {}", addressNode);


    }
}
