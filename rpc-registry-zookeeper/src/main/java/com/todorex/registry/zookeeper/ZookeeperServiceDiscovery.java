package com.todorex.registry.zookeeper;


import com.todorex.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * zookeeper 服务发现实现
 *
 * @Author rex
 * 2018/8/8
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery{

    private String zkAddress;

    public ZookeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String serviceName) {
        // 创建Zookeeper客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        log.debug("connect zookeeper");
        try {
            // 获取service节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" +serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find the path: %s", servicePath));
            }
            // 获取节点列表
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtils.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on the path: %s", servicePath));
            }
            // 获取address节点
            String address;
            int size = addressList.size();
            if (size == 1) {
                address = addressList.get(0);
                log.debug("get only one address node: {}", address);
            } else {
                // 若存在多个地址，则随机获取一个地址
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                log.debug("get random address node: {}", address);
            }
            // 获取节点的值
            String addressPath = servicePath + "/" + address;
            return  zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }
}
